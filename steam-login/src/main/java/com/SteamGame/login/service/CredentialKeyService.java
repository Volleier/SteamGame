package com.SteamGame.login.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 本地加密密钥管理 — 密钥存储在 auth.yaml 的 security.credentialKey 节点。
 * 不依赖环境变量；首次使用时自动生成 256-bit AES Key。
 */
@Service
public class CredentialKeyService {

    private static final Logger logger = LoggerFactory.getLogger(CredentialKeyService.class);

    @Value("${login.config.path:auth.yaml}")
    private String configPath;

    private static final DateTimeFormatter TF = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    /**
     * 读取或创建本地加密密钥。
     * 如果 auth.yaml 不存在：创建仅有 security.credentialKey 的最小文件。
     * 如果存在但无 security.credentialKey：生成密钥并合并写入（保留 auth 区不动）。
     */
    public String loadOrCreateKey() {
        try {
            File f = new File(configPath);
            if (!f.exists()) {
                return createNewKeyFile();
            }

            // read existing
            Yaml yaml = new Yaml();
            Map<String, Object> root;
            try (InputStream is = new FileInputStream(f)) {
                Object loaded = yaml.load(is);
                root = loaded instanceof Map ? cast(loaded) : new LinkedHashMap<>();
            }

            // check security.credentialKey
            Map<String, Object> security = getOrCreateSection(root, "security");
            Map<String, Object> credKey = getOrCreateSection(security, "credentialKey");

            String existingKey = (String) credKey.get("base64Key");
            if (existingKey != null && !existingKey.isEmpty()) {
                logger.debug("从 auth.yaml 加载本地加密密钥成功");
                return existingKey;
            }

            // key missing — generate and write
            String newKey = generateBase64Key();
            credKey.put("algorithm", "AES-256-GCM");
            credKey.put("base64Key", newKey);
            credKey.put("createdAt", OffsetDateTime.now(ZoneOffset.UTC).format(TF));

            security.put("credentialKey", credKey);
            root.put("security", security);

            writeAtomic(root);

            logger.info("已生成新的本地加密密钥并写入 auth.yaml");
            return newKey;

        } catch (Exception e) {
            logger.error("无法加载或创建本地加密密钥: {}", e.getMessage(), e);
            throw new RuntimeException("无法初始化本地加密密钥", e);
        }
    }

    /**
     * 只读取已有密钥，不创建。
     */
    public String loadKey() {
        try {
            File f = new File(configPath);
            if (!f.exists()) return null;

            Yaml yaml = new Yaml();
            Map<String, Object> root;
            try (InputStream is = new FileInputStream(f)) {
                Object loaded = yaml.load(is);
                if (!(loaded instanceof Map)) return null;
                root = cast(loaded);
            }

            Map<String, Object> security = (Map<String, Object>) root.get("security");
            if (security == null) return null;

            Map<String, Object> credKey = (Map<String, Object>) security.get("credentialKey");
            if (credKey == null) return null;

            return (String) credKey.get("base64Key");
        } catch (Exception e) {
            logger.warn("读取本地加密密钥失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 确保 auth.yaml 存在最小结构（首次启动、未配置凭据时亦可调用）。
     * 不覆盖已有的 auth 或 security 区。
     */
    public void ensureAuthYamlExists() {
        File f = new File(configPath);
        if (f.exists()) return;

        try {
            Map<String, Object> root = new LinkedHashMap<>();
            String key = generateBase64Key();

            Map<String, Object> credKey = new LinkedHashMap<>();
            credKey.put("algorithm", "AES-256-GCM");
            credKey.put("base64Key", key);
            credKey.put("createdAt", OffsetDateTime.now(ZoneOffset.UTC).format(TF));

            Map<String, Object> security = new LinkedHashMap<>();
            security.put("credentialKey", credKey);
            root.put("security", security);

            writeAtomic(root);
            logger.info("已创建 auth.yaml（含 security.credentialKey）");
        } catch (Exception e) {
            logger.error("创建 auth.yaml 失败: {}", e.getMessage(), e);
            throw new RuntimeException("无法创建 auth.yaml", e);
        }
    }

    // ===== private helpers =====

    private String createNewKeyFile() {
        String key = generateBase64Key();
        Map<String, Object> root = new LinkedHashMap<>();

        Map<String, Object> credKey = new LinkedHashMap<>();
        credKey.put("algorithm", "AES-256-GCM");
        credKey.put("base64Key", key);
        credKey.put("createdAt", OffsetDateTime.now(ZoneOffset.UTC).format(TF));

        Map<String, Object> security = new LinkedHashMap<>();
        security.put("credentialKey", credKey);
        root.put("security", security);

        writeAtomic(root);
        logger.info("已创建 auth.yaml（含本地加密密钥）");
        return key;
    }

    private String generateBase64Key() {
        try {
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(256, new SecureRandom());
            SecretKey sk = kg.generateKey();
            return Base64.getEncoder().encodeToString(sk.getEncoded());
        } catch (Exception e) {
            throw new RuntimeException("生成 AES 密钥失败", e);
        }
    }

    private void writeAtomic(Map<String, Object> root) {
        try {
            DumperOptions opts = new DumperOptions();
            opts.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            opts.setPrettyFlow(true);
            Yaml yaml = new Yaml(opts);

            File target = new File(configPath);
            File tmp = new File(configPath + ".tmp");
            try (FileWriter w = new FileWriter(tmp)) {
                yaml.dump(root, w);
            }
            Files.move(tmp.toPath(), target.toPath(),
                    StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (Exception e) {
            throw new RuntimeException("原子写入 auth.yaml 失败", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> cast(Object obj) {
        return (Map<String, Object>) obj;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getOrCreateSection(Map<String, Object> root, String key) {
        Object obj = root.get(key);
        if (obj instanceof Map) return (Map<String, Object>) obj;
        Map<String, Object> section = new LinkedHashMap<>();
        root.put(key, section);
        return section;
    }
}
