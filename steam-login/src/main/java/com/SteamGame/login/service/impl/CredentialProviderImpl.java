package com.SteamGame.login.service.impl;

import com.SteamGame.api.domain.SteamCredential;
import com.SteamGame.api.service.CredentialProvider;
import com.SteamGame.login.service.CredentialKeyService;
import com.SteamGame.login.service.CredentialSessionStore;
import com.SteamGame.util.CryptoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

/**
 * CredentialProvider 默认实现 —— 优先读取内存会话，其次从 auth.yaml 解密获取凭证。
 * <p>
 * MVP 阶段内存会话（rememberMe=false）中暂未保存真实 apiKey，
 * 实际生效路径为 auth.yaml 解密。
 */
@Service
public class CredentialProviderImpl implements CredentialProvider {

    private static final Logger logger = LoggerFactory.getLogger(CredentialProviderImpl.class);

    @Value("${login.config.path:auth.yaml}")
    private String configPath;

    @Autowired
    private CredentialKeyService keyService;

    @Autowired(required = false)
    private CredentialSessionStore sessionStore;

    @Override
    public SteamCredential getCurrentCredential(String userId) {
        String uid = userIdOrDefault(userId);

        // 1) 优先检查内存会话
        if (sessionStore != null && sessionStore.hasCredential(uid)) {
            var session = sessionStore.get(uid);
            if (session.isPresent()) {
                var entry = session.get();
                if (entry.encryptedApiKey != null && !entry.encryptedApiKey.isEmpty()) {
                    // 内存会话中存有加密 apiKey，用本地密钥解密
                    try {
                        String base64Key = keyService.loadKey();
                        if (base64Key != null && !base64Key.isEmpty()) {
                            String plainKey = CryptoUtil.decrypt(entry.encryptedApiKey, entry.iv, base64Key);
                            logger.info("从内存会话获取并解密凭证成功 — steamId={}", entry.steamId);
                            return new SteamCredential(uid, entry.steamId, plainKey);
                        }
                    } catch (Exception e) {
                        logger.warn("解密内存会话 apiKey 失败: {}", e.getMessage());
                    }
                }
                // 内存会话存在但 apiKey 为空 — MVP 阶段 rememberMe=false 不存真 apiKey
                logger.debug("内存会话存在但 apiKey 为空，回退到 auth.yaml");
            }
        }

        // 2) 回退：从 auth.yaml 读取并解密
        return readFromYaml(uid);
    }

    private SteamCredential readFromYaml(String userId) {
        try {
            File f = new File(configPath);
            if (!f.exists()) {
                logger.warn("auth.yaml 不存在，无法提供凭证");
                return emptyCredential(userId);
            }

            Yaml yaml = new Yaml();
            Map<String, Object> root;
            try (InputStream inputStream = new FileInputStream(f)) {
                Object loaded = yaml.load(inputStream);
                root = loaded instanceof Map ? cast(loaded) : null;
            }

            if (root == null || !root.containsKey("auth")) {
                logger.warn("auth.yaml 中未找到 auth 节点");
                return emptyCredential(userId);
            }

            Map<String, Object> authData = cast(root.get("auth"));
            String steamId = extractString(authData, "steamId");
            Object apiKeyEncryptedObj = authData.get("apiKeyEncrypted");
            Object keyMetaObj = authData.get("keyMeta");

            if (steamId == null || steamId.isEmpty()) {
                logger.warn("auth.yaml 中未找到 steamId");
                return emptyCredential(userId);
            }

            if (apiKeyEncryptedObj == null || !(keyMetaObj instanceof Map)) {
                logger.warn("auth.yaml 中未找到加密 apiKey 或 keyMeta");
                return emptyCredential(userId);
            }

            Map<String, Object> keyMeta = cast(keyMetaObj);
            String iv = extractString(keyMeta, "iv");
            String cipherText = apiKeyEncryptedObj.toString();

            if (iv == null) {
                logger.warn("keyMeta 中缺少 iv");
                return emptyCredential(userId);
            }

            // 从 auth.yaml security.credentialKey 读取本地加密密钥
            String base64Key = keyService.loadKey();
            if (base64Key == null || base64Key.isEmpty()) {
                logger.warn("auth.yaml 未找到本地加密密钥");
                return emptyCredential(userId);
            }

            // 解密
            String plainApiKey = CryptoUtil.decrypt(cipherText, iv, base64Key);
            logger.info("从 auth.yaml 解密凭据成功 — steamId={}", steamId);
            return new SteamCredential(userId, steamId, plainApiKey);

        } catch (Exception e) {
            logger.error("从 auth.yaml 读取凭据失败: {}", e.getMessage(), e);
            return emptyCredential(userId);
        }
    }

    private SteamCredential emptyCredential(String userId) {
        return new SteamCredential(userId, null, null);
    }

    private String userIdOrDefault(String userId) {
        return userId != null && !userId.isEmpty() ? userId : "default";
    }

    private String extractString(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val == null ? null : val.toString().trim();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> cast(Object obj) {
        return (Map<String, Object>) obj;
    }
}
