package com.SteamGame.login.service.impl;

import com.SteamGame.login.dto.CredentialViewDTO;
import com.SteamGame.login.dto.CredentialCheckResult;
import com.SteamGame.login.dto.ApiResponse;
import com.SteamGame.login.dto.ResultCode;
import com.SteamGame.login.service.CredentialKeyService;
import com.SteamGame.login.service.CredentialValidationService;
import com.SteamGame.login.service.CredentialVerifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

@Service
public class CredentialVerifyServiceImpl implements CredentialVerifyService {

    private static final Logger logger = LoggerFactory.getLogger(CredentialVerifyServiceImpl.class);

    @Value("${login.config.path:auth.yaml}")
    private String configPath;

    @Autowired
    private CredentialKeyService keyService;

    @Autowired(required = false)
    private CredentialValidationService validationService;

    @Autowired(required = false)
    private com.SteamGame.login.service.CredentialSessionStore sessionStore;

    @Override
    public CredentialViewDTO readCredentialFromYaml() {
        try {
            File f = new File(configPath);
            if (!f.exists()) {
                return new CredentialViewDTO();
            }

            Yaml yaml = new Yaml();
            Map<String, Object> loginData;
            try (InputStream inputStream = new FileInputStream(f)) {
                loginData = (Map<String, Object>) yaml.load(inputStream);
            }

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
                    Map<String, Object> keyMeta = (Map<String, Object>) keyMetaObj;
                    Object ivObj = keyMeta.get("iv");
                    String iv = ivObj == null ? null : ivObj.toString();

                    // 从 auth.yaml security.credentialKey 读取密钥
                    String base64Key = keyService.loadKey();
                    if (base64Key == null || base64Key.isEmpty()) {
                        logger.warn("auth.yaml 未找到本地加密密钥，标记为无可用凭据");
                        hasKey = false;
                    } else if (iv == null) {
                        logger.warn("存储的 keyMeta 中缺少 iv，标记为无可用凭据");
                        hasKey = false;
                    } else {
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
        // 1) check session store first
        if (sessionStore != null && sessionStore.hasCredential("default")) {
            var session = sessionStore.get("default");
            if (session.isPresent()) {
                CredentialViewDTO view = new CredentialViewDTO();
                view.setSteamId(session.get().steamId);
                view.setHasApiKey(true);
                view.setUpdatedAt(null);
                return ApiResponse.ok(ResultCode.LOGIN_OK, view, "凭据状态返回（内存会话）");
            }
        }

        // 2) fallback to YAML
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
        // 1) check session store for in-memory credentials (rememberMe=false case)
        if (sessionStore != null && sessionStore.hasCredential("default")) {
            var session = sessionStore.get("default");
            if (session.isPresent()) {
                var entry = session.get();
                if (entry.authenticated && entry.validKeyAndUser) {
                    CredentialCheckResult result = new CredentialCheckResult(
                            entry.validKeyAndUser, entry.ownsGames,
                            entry.profilePrivate, "内存会话：凭据有效", entry.gameCount);
                    return ApiResponse.ok(ResultCode.LOGIN_OK, result, "凭据有效（内存会话）");
                }
            }
        }

        // 2) fallback to YAML
        CredentialViewDTO view = readCredentialFromYaml();

        String steamid = view.getSteamId();
        File f = new File(configPath);
        if (!f.exists() || steamid == null || steamid.isEmpty() || !view.isHasApiKey()) {
            return ApiResponse.fail(ResultCode.CONFIG_NOT_FOUND, "未找到凭据配置或配置不完整");
        }

        // 解密并在线验证
        try {
            Yaml yaml = new Yaml();
            Map<String, Object> loginData;
            try (InputStream inputStream = new FileInputStream(configPath)) {
                loginData = (Map<String, Object>) yaml.load(inputStream);
            }
            Map<String, Object> authData = (Map<String, Object>) loginData.get("auth");
            String cipherText = authData.get("apiKeyEncrypted").toString();
            Map<String, Object> keyMeta = (Map<String, Object>) authData.get("keyMeta");
            String iv = keyMeta.get("iv").toString();

            // 从 auth.yaml 读取本地加密密钥
            String base64Key = keyService.loadKey();
            if (base64Key == null || base64Key.isEmpty()) {
                return ApiResponse.fail(ResultCode.DECRYPT_FAILED, "auth.yaml 未找到本地加密密钥");
            }

            String plainKey;
            try {
                plainKey = com.SteamGame.util.CryptoUtil.decrypt(cipherText, iv, base64Key);
            } catch (Exception ex) {
                logger.error("解密失败", ex);
                return ApiResponse.fail(ResultCode.DECRYPT_FAILED, "凭据解密失败");
            }

            // 在线校验
            try {
                CredentialCheckResult result;
                if (validationService != null) {
                    try {
                        result = validationService.validateOnline(steamid, plainKey);
                    } catch (Exception ex) {
                        logger.error("在线校验抛出异常，映射为 STEAM_API_UNAVAILABLE", ex);
                        return ApiResponse.fail(ResultCode.STEAM_API_UNAVAILABLE, "Steam API 无法访问");
                    }
                } else {
                    logger.warn("未注入 CredentialValidationService，无法执行在线校验");
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
