package com.SteamGame.login.service.impl;

import com.SteamGame.login.dto.ApiResponse;
import com.SteamGame.login.dto.CredentialCheckResult;
import com.SteamGame.login.dto.CredentialInputDTO;
import com.SteamGame.login.dto.CredentialViewDTO;
import com.SteamGame.login.dto.ResultCode;
import com.SteamGame.login.model.CredentialRecord;
import com.SteamGame.login.model.CredentialValidationMeta;
import com.SteamGame.login.repository.CredentialRepository;
import com.SteamGame.login.service.CredentialConfigService;
import com.SteamGame.login.service.CredentialService;
import com.SteamGame.login.service.CredentialSessionStore;
import com.SteamGame.login.service.CredentialValidationService;
import com.SteamGame.login.service.CredentialVerifyService;
import com.SteamGame.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Service
public class CredentialServiceImpl implements CredentialService {

    private static final Logger logger = LoggerFactory.getLogger(CredentialServiceImpl.class);

    @Autowired
    private CredentialConfigService configService;

    @Autowired
    private CredentialVerifyService verifyService;

    @Autowired
    private CredentialSessionStore sessionStore;

    @Autowired(required = false)
    private CredentialValidationService validationService;

    @Autowired(required = false)
    private com.SteamGame.login.service.CredentialCachePolicy cachePolicy;

    @Autowired(required = false)
    private com.SteamGame.login.config.CredentialProperties credentialProperties;

    @Autowired(required = false)
    private CredentialRepository credentialRepository;

    private static final DateTimeFormatter TF = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    // ========== P0-4: configureAndLogin ==========

    @Override
    public ApiResponse<Object> registerAndValidate(com.SteamGame.login.dto.RegisterCredentialRequest req) {
        String steamId = req.getSteamId() == null ? null : req.getSteamId().trim();
        String apiKey = req.getApiKey() == null ? null : req.getApiKey().trim();
        boolean rememberMe = req.isRememberMe();

        // Step 1: format validation
        ResultCode formatError = ValidationUtil.validateCredentialInput(steamId, apiKey);
        if (formatError != null) {
            String msg;
            if (formatError == ResultCode.INVALID_STEAM_ID) {
                msg = "Steam ID 应为 17 位数字";
            } else {
                msg = "API Key 通常为 32 位十六进制字符串";
            }
            logger.warn("输入格式校验失败 — steamId={} code={}", steamId, formatError);
            return ApiResponse.fail(formatError, msg);
        }

        // Step 2: online validation
        CredentialCheckResult result;
        try {
            if (validationService != null) {
                result = validationService.validateOnline(steamId, apiKey);
            } else {
                logger.error("未注入 CredentialValidationService");
                return ApiResponse.fail(ResultCode.STEAM_API_UNAVAILABLE, "Steam 校验服务未配置");
            }
        } catch (Exception e) {
            String msg = e.getMessage();
            logger.error("在线验证异常: {}", msg, e);
            if (msg != null && msg.contains("STEAM_API_TIMEOUT")) {
                return ApiResponse.fail(ResultCode.STEAM_API_TIMEOUT, "Steam API 请求超时，请稍后重试");
            }
            return ApiResponse.fail(ResultCode.STEAM_API_UNAVAILABLE, "Steam 服务不可用，请稍后重试");
        }

        if (!result.isValidKeyAndUser()) {
            return ApiResponse.fail(ResultCode.INVALID_KEY_OR_USER, result.getMessage());
        }

        // Step 3: determine result code
        ResultCode resultCode;
        if (result.isProfilePrivate() || !result.isOwnsGames()) {
            resultCode = ResultCode.PROFILE_PRIVATE_OR_NO_GAMES;
        } else {
            resultCode = ResultCode.LOGIN_OK;
        }

        // Step 4: persist or store in-memory
        String userId = req.getUserId() != null ? req.getUserId() : "default";
        try {
            if (rememberMe) {
                // save encrypted to auth.yaml
                CredentialInputDTO dto = new CredentialInputDTO();
                dto.setSteamId(steamId);
                dto.setApiKey(apiKey);
                ApiResponse<Object> saveResp = configService.saveCredentialWithValidation(dto, result);
                if (saveResp == null || !saveResp.isSuccess()) {
                    return saveResp == null
                            ? ApiResponse.fail(ResultCode.INTERNAL_ERROR, "保存配置失败")
                            : saveResp;
                }
                logger.info("凭据已持久化到 auth.yaml — steamId={} rememberMe=true", steamId);
            } else {
                // store in memory only
                sessionStore.save(userId, steamId, "", "");
                sessionStore.setValidationResult(userId, result.isValidKeyAndUser(),
                        result.isOwnsGames(), result.isProfilePrivate(),
                        result.getGameCount() != null ? result.getGameCount() : 0);
                logger.info("凭据已存入内存会话 — steamId={} rememberMe=false", steamId);
            }

            // also update repository metadata if available
            tryUpdateRepository(userId, steamId, result);

        } catch (Exception e) {
            logger.error("保存凭据时异常: {}", e.getMessage(), e);
            return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "保存凭据失败");
        }

        return ApiResponse.ok(resultCode, result, resultCode == ResultCode.LOGIN_OK ? "配置并验证成功" : "验证部分成功（游戏列表可能受限）");
    }

    // ========== P0-9: loadAndValidateForLogin ==========

    @Override
    public ApiResponse<CredentialCheckResult> loadAndValidateForLogin(String userId) {
        String uid = userIdOrDefault(userId);

        // 1) check in-memory session first
        if (sessionStore.hasCredential(uid)) {
            var session = sessionStore.get(uid);
            if (session.isPresent()) {
                var entry = session.get();
                if (entry.authenticated && entry.validKeyAndUser) {
                    CredentialCheckResult cached = new CredentialCheckResult(
                            entry.validKeyAndUser, entry.ownsGames,
                            entry.profilePrivate, "内存会话：凭据有效", entry.gameCount);
                    return ApiResponse.ok(ResultCode.LOGIN_OK, cached, "凭据有效（内存会话）");
                }
            }
        }

        // 2) check repository cache
        if (credentialRepository != null) {
            try {
                var or = credentialRepository.findByUserId(uid);
                if (or.isPresent()) {
                    CredentialRecord r = or.get();
                    CredentialValidationMeta m = r.getValidation();
                    if (m != null && cachePolicy != null && cachePolicy.isValidationFresh(m)) {
                        CredentialCheckResult cached = new CredentialCheckResult(true, true, false,
                                "缓存校验通过", 0);
                        return ApiResponse.ok(ResultCode.LOGIN_OK, cached, "缓存命中：凭据有效");
                    }
                }
            } catch (Exception e) {
                logger.warn("从 repository 加载凭据时发生异常: {}", e.getMessage());
            }
        }

        // 3) fallback: online re-verify via verifyService
        ApiResponse<CredentialCheckResult> validateResp = verifyService.validateCredential();
        if (validateResp != null && validateResp.isSuccess()) {
            tryUpdateRepository(uid, null, validateResp.getData());
        }
        if (validateResp == null) {
            return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "在线校验未返回结果");
        }
        return validateResp;
    }

    // ========== P0-9: getCredentialStatus ==========

    @Override
    public ApiResponse<CredentialViewDTO> getCredentialStatus(String userId) {
        String uid = userIdOrDefault(userId);

        // 1) check session store
        if (sessionStore.hasCredential(uid)) {
            var session = sessionStore.get(uid);
            if (session.isPresent()) {
                var entry = session.get();
                CredentialViewDTO view = new CredentialViewDTO();
                view.setSteamId(entry.steamId);
                view.setHasApiKey(true);
                view.setUpdatedAt(null);
                return ApiResponse.ok(ResultCode.LOGIN_OK, view, "凭据状态返回（内存会话）");
            }
        }

        // 2) check repository
        try {
            if (credentialRepository != null) {
                var or = credentialRepository.findByUserId(uid);
                if (or.isPresent()) {
                    CredentialRecord r = or.get();
                    CredentialViewDTO view = new CredentialViewDTO();
                    view.setSteamId(r.getSteamId());
                    view.setUpdatedAt(r.getValidation() == null ? null : r.getValidation().getLastValidatedAt());
                    view.setHasApiKey(r.getApiKey() != null);
                    return ApiResponse.ok(ResultCode.LOGIN_OK, view, "凭据状态返回");
                }
            }
        } catch (Exception e) {
            logger.warn("从 repository 获取凭据状态时失败: {}", e.getMessage());
        }

        // 3) fallback to YAML file
        return verifyService.sendCredentialInfoToFrontend();
    }

    // ========== helpers ==========

    private String userIdOrDefault(String userId) {
        return userId != null && !userId.isEmpty() ? userId : "default";
    }

    private void tryUpdateRepository(String userId, String steamId, CredentialCheckResult result) {
        try {
            if (credentialRepository != null) {
                CredentialRecord r = new CredentialRecord();
                r.setUserId(userIdOrDefault(userId));
                r.setSteamId(steamId);
                r.setApiKey(null);
                CredentialValidationMeta meta = new CredentialValidationMeta();
                boolean success = result != null && result.isValidKeyAndUser();
                meta.setStatus(success ? "VALID" : "INVALID");
                meta.setLastValidatedAt(LocalDateTime.now(ZoneOffset.UTC).format(TF));
                if (cachePolicy != null) {
                    meta.setNextRevalidateAt(cachePolicy.computeNextRevalidateAt(success));
                } else {
                    meta.setNextRevalidateAt(LocalDateTime.now(ZoneOffset.UTC).plusHours(success ? 6 : 1).format(TF));
                }
                meta.setFailCount(success ? 0 : 1);
                meta.setLastErrorCode(success ? "" : "INVALID_KEY_OR_USER");
                r.setValidation(meta);
                credentialRepository.upsert(r);
            }
        } catch (Exception e) {
            logger.debug("更新 repository 元信息失败（非阻塞）: {}", e.getMessage());
        }
    }
}
