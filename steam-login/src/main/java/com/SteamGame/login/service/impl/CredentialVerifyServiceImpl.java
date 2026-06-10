package com.SteamGame.login.service.impl;

import com.SteamGame.login.dto.CredentialViewDTO;
import com.SteamGame.login.dto.CredentialCheckResult;
import com.SteamGame.login.dto.ApiResponse;
import com.SteamGame.login.dto.ResultCode;
import com.SteamGame.login.service.CredentialVerifyService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.yaml.snakeyaml.Yaml;
import com.SteamGame.login.service.CredentialValidationService;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Service
public class CredentialVerifyServiceImpl implements CredentialVerifyService {

    private static final Logger logger = LoggerFactory.getLogger(CredentialVerifyServiceImpl.class);

    @Value("${login.config.path:auth.yaml}")
    private String configPath;

    @Value("${login.encryption.base64Key:}")
    private String base64Key;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired(required = false)
    private CredentialValidationService validationService;

    public CredentialVerifyServiceImpl() {
        // RestTemplate still present for legacy usage in case a validation service is
        // not injected
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(4000);
        requestFactory.setReadTimeout(4000);
        // no direct field for RestTemplate needed when using injected validationService
    }

    @Override
    public CredentialViewDTO readCredentialFromYaml() {
        try {
            Yaml yaml = new Yaml();
            InputStream inputStream = new FileInputStream(configPath);
            Map<String, Object> loginData = yaml.load(inputStream);
            CredentialViewDTO view = new CredentialViewDTO();
            if (loginData != null && loginData.containsKey("auth")) {
                Map<String, Object> authData = (Map<String, Object>) loginData.get("auth");
                Object steamIdObj = authData.get("steamId");
                Object apiKeyEncryptedObj = authData.get("apiKeyEncrypted");
                Object keyMetaObj = authData.get("keyMeta");
                Object timeObj = authData.get("updatedAt");

                String steamId = steamIdObj == null ? null : steamIdObj.toString().trim();
                String time = timeObj == null ? null : timeObj.toString().trim();

                view.setSteamId(steamId);
                view.setUpdatedAt(time);

                boolean hasKey = false;
                if (apiKeyEncryptedObj != null && keyMetaObj instanceof Map) {
                    String cipherText = apiKeyEncryptedObj.toString();
                    Map<String, Object> keyMeta = (Map<String, Object>) keyMetaObj;
                    Object ivObj = keyMeta.get("iv");
                    String iv = ivObj == null ? null : ivObj.toString();

                    if (base64Key == null || base64Key.isEmpty()) {
                        logger.warn("未配置解密密钥，标记为无可用凭据");
                        hasKey = false;
                    } else if (iv == null) {
                        logger.warn("存储的 keyMeta 中缺少 iv，标记为无可用凭据");
                        hasKey = false;
                    } else {
                        // 不在此处解密，仅标记为存在密文
                        hasKey = true;
                    }
                }

                view.setHasApiKey(hasKey);
            }
            logger.info("从YAML文件读取的凭据信息 - SteamID: {}, hasApiKey: {}",
                    view.getSteamId(), view.isHasApiKey());

            return view;
        } catch (FileNotFoundException e) {
            logger.warn("未找到凭据配置文件: {}（首次启动或尚未配置凭据）", configPath);
            return new CredentialViewDTO();
        } catch (Exception e) {
            logger.error("读取凭据配置文件时发生错误", e);
            return new CredentialViewDTO();
        }
    }

    @Override
    public ApiResponse<CredentialViewDTO> sendCredentialInfoToFrontend() {
        CredentialViewDTO view = readCredentialFromYaml();
        logger.info("准备发送到前端的凭据信息 - SteamID: {}, hasApiKey: {}",
                view.getSteamId(), view.isHasApiKey());
        if (view.getSteamId() == null && !view.isHasApiKey()) {
            return ApiResponse.fail(ResultCode.CONFIG_NOT_FOUND, "未找到凭据配置");
        }
        return ApiResponse.ok(ResultCode.LOGIN_OK, view, "凭据状态已返回");
    }

    @Override
    public ApiResponse<CredentialCheckResult> validateCredential() {
        CredentialViewDTO view = readCredentialFromYaml();

        String steamid = view.getSteamId();
        // Check presence
        java.io.File f = new java.io.File(configPath);
        if (!f.exists() || steamid == null || steamid.isEmpty() || !view.isHasApiKey()) {
            return ApiResponse.fail(ResultCode.CONFIG_NOT_FOUND, "未找到凭据配置或配置不完整");
        }

        // 尝试解密并验证
        try {
            // 从文件中读取密文并解密
            Yaml yaml = new Yaml();
            InputStream inputStream = new FileInputStream(configPath);
            Map<String, Object> loginData = yaml.load(inputStream);
            Map<String, Object> authData = (Map<String, Object>) loginData.get("auth");
            String cipherText = authData.get("apiKeyEncrypted").toString();
            Map<String, Object> keyMeta = (Map<String, Object>) authData.get("keyMeta");
            String iv = keyMeta.get("iv").toString();

            if (base64Key == null || base64Key.isEmpty()) {
                return ApiResponse.fail(ResultCode.DECRYPT_FAILED, "服务器未配置解密密钥");
            }

            String plainKey;
            try {
                plainKey = com.SteamGame.util.CryptoUtil.decrypt(cipherText, iv, base64Key);
            } catch (Exception ex) {
                logger.error("解密失败", ex);
                return ApiResponse.fail(ResultCode.DECRYPT_FAILED, "凭据解密失败");
            }

            // 使用抽离的 CredentialValidationService 进行在线校验（若可用），否则保留原逻辑
            try {
                CredentialCheckResult result;
                if (validationService != null) {
                    try {
                        result = validationService.validateOnline(steamid, plainKey);
                    } catch (Exception ex) {
                        // 将外部服务异常映射为 STEAM API 不可用
                        logger.error("在线校验抛出异常，映射为 STEAM_API_UNAVAILABLE", ex);
                        return ApiResponse.fail(ResultCode.STEAM_API_UNAVAILABLE, "Steam API 无法访问");
                    }
                } else {
                    // 回退到内联实现（保留原有 RestTemplate 逻辑）
                    // 为避免代码重复，这里简单返回不可用，建议注入 validationService
                    logger.warn("未注入 CredentialValidationService，无法执行在线校验（回退被禁用）");
                    return ApiResponse.fail(ResultCode.STEAM_API_UNAVAILABLE, "Steam 校验服务未配置");
                }

                if (result == null) {
                    return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "在线校验未返回结果");
                }
                if (!result.isValidKeyAndUser()) {
                    return ApiResponse.fail(ResultCode.INVALID_KEY_OR_USER, result.getMessage());
                }
                return ApiResponse.ok(ResultCode.LOGIN_OK, result, "凭据验证成功");
            } catch (Exception e) {
                logger.error("处理凭据验证时发生错误", e);
                return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "凭据验证过程发生内部错误");
            }

        } catch (Exception e) {
            logger.error("处理凭据验证时发生错误", e);
            return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "凭据验证过程发生内部错误");
        }
    }
}
