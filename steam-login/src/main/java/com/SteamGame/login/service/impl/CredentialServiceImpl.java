package com.SteamGame.login.service.impl;

import com.SteamGame.login.dto.ApiResponse;
import com.SteamGame.login.dto.CredentialCheckResult;
import com.SteamGame.login.dto.CredentialInputDTO;
import com.SteamGame.login.dto.ResultCode;
import com.SteamGame.login.model.CredentialRecord;
import com.SteamGame.login.model.CredentialValidationMeta;
import com.SteamGame.login.repository.CredentialRepository;
import com.SteamGame.login.service.CredentialConfigService;
import com.SteamGame.login.service.CredentialService;
import com.SteamGame.login.service.CredentialVerifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneOffset;

@Service
public class CredentialServiceImpl implements CredentialService {

    private static final Logger logger = LoggerFactory.getLogger(CredentialServiceImpl.class);

    @Autowired
    private CredentialConfigService configService;

    @Autowired
    private CredentialVerifyService verifyService;

    @Autowired(required = false)
    private CredentialRepository credentialRepository;

    private static final DateTimeFormatter TF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public ApiResponse<Object> registerAndValidate(CredentialInputDTO dto) {
        // 保存到配置（delegation to existing implementation）
        ApiResponse<Object> saveResp = configService.saveCredentialInfo(dto);
        if (saveResp == null || !saveResp.isSuccess()) {
            logger.warn("保存凭据失败，直接返回失败响应");
            return saveResp == null ? ApiResponse.fail(ResultCode.INTERNAL_ERROR, "保存凭据失败") : saveResp;
        }

        // 保存成功后立即在线校验并返回校验结果
        ApiResponse<CredentialCheckResult> validateResp = verifyService.validateCredential();

        // 若 repository 可用，则尝试写入一份最小记录（用于后续调度），不阻塞主流程
        try {
            if (credentialRepository != null) {
                CredentialRecord r = new CredentialRecord();
                // current DTO does not carry userId in this iteration; use default placeholder
                r.setUserId("default");
                r.setSteamId(dto.getSteamId());
                // 源凭据已被 configService 加密存储，repository 在现阶段保存占位或明文视实现而定
                r.setApiKey(null);

                CredentialValidationMeta meta = new CredentialValidationMeta();
                if (validateResp != null && validateResp.isSuccess()) {
                    meta.setStatus("VALID");
                    meta.setLastValidatedAt(LocalDateTime.now(ZoneOffset.UTC).format(TF));
                    // 简单策略：6 小时后重校验
                    meta.setNextRevalidateAt(LocalDateTime.now(ZoneOffset.UTC).plusHours(6).format(TF));
                    meta.setFailCount(0);
                    meta.setLastErrorCode("");
                } else {
                    meta.setStatus("INVALID");
                    meta.setLastValidatedAt(LocalDateTime.now(ZoneOffset.UTC).format(TF));
                    meta.setNextRevalidateAt(LocalDateTime.now(ZoneOffset.UTC).plusHours(1).format(TF));
                    meta.setFailCount(1);
                    meta.setLastErrorCode(validateResp == null ? "UNKNOWN" : String.valueOf(validateResp.getCode()));
                }
                r.setValidation(meta);
                try {
                    credentialRepository.upsert(r);
                } catch (Exception ex) {
                    logger.warn("向 CredentialRepository 写入校验元信息失败（非致命）: {}", ex.getMessage());
                }
            }
        } catch (Exception ex) {
            logger.warn("注册/校验后尝试持久化元信息发生异常，但不影响主流：{}", ex.getMessage());
        }

        // 将校验响应（若存在）返回给上层控制器
        if (validateResp == null) {
            return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "在线校验未返回结果");
        }
        return ApiResponse.ok(validateResp.getCode(), validateResp.getData(), validateResp.getMessage());
    }

    @Override
    public ApiResponse<CredentialCheckResult> loadAndValidateForLogin(String userId) {
        // 优先通过 repository 加载（若支持）
        if (credentialRepository != null) {
            try {
                java.util.Optional<com.SteamGame.login.model.CredentialRecord> or = credentialRepository
                        .findByUserId(userId);
                if (or.isPresent()) {
                    com.SteamGame.login.model.CredentialRecord r = or.get();
                    CredentialValidationMeta m = r.getValidation();
                    // 若有元信息且 nextRevalidateAt 晚于当前时间，则直接返回通过
                    if (m != null && m.getNextRevalidateAt() != null) {
                        try {
                            java.time.LocalDateTime next = java.time.LocalDateTime.parse(m.getNextRevalidateAt(), TF);
                            if (next.isAfter(java.time.LocalDateTime.now(ZoneOffset.UTC))) {
                                // 返回一个简单成功结果（无需再次访问 Steam）
                                CredentialCheckResult simple = new CredentialCheckResult(true, true, false,
                                        "缓存校验通过", 0);
                                return ApiResponse.ok(ResultCode.LOGIN_OK, simple, "缓存命中：凭据有效");
                            }
                        } catch (Exception ex) {
                            // 解析失败，继续触发在线校验
                            logger.debug("解析 nextRevalidateAt 失败，将触发在线校验: {}", ex.getMessage());
                        }
                    }
                }
            } catch (Exception e) {
                logger.warn("从 repository 加载凭据时发生异常，将继续触发在线校验: {}", e.getMessage());
            }
        }

        // 触发在线校验（使用现有实现）
        ApiResponse<CredentialCheckResult> validateResp = verifyService.validateCredential();

        // 若校验成功且 repository 可用，则更新 meta
        try {
            if (validateResp != null && validateResp.isSuccess() && credentialRepository != null) {
                com.SteamGame.login.model.CredentialRecord r = new com.SteamGame.login.model.CredentialRecord();
                r.setUserId(userId == null ? "default" : userId);
                r.setSteamId(null);
                r.setApiKey(null);
                CredentialValidationMeta meta = new CredentialValidationMeta();
                meta.setStatus("VALID");
                meta.setLastValidatedAt(LocalDateTime.now(ZoneOffset.UTC).format(TF));
                meta.setNextRevalidateAt(LocalDateTime.now(ZoneOffset.UTC).plusHours(6).format(TF));
                meta.setFailCount(0);
                meta.setLastErrorCode("");
                r.setValidation(meta);
                try {
                    credentialRepository.upsert(r);
                } catch (Exception ex) {
                    logger.debug("在线校验后更新 repository 元信息失败（非阻塞）: {}", ex.getMessage());
                }
            }
        } catch (Exception ex) {
            logger.debug("尝试在校验后更新 repository 时发生异常: {}", ex.getMessage());
        }

        if (validateResp == null) {
            return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "在线校验未返回结果");
        }
        return validateResp;
    }
}
