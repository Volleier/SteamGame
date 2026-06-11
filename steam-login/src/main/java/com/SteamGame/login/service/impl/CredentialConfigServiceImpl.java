package com.SteamGame.login.service.impl;

import com.SteamGame.login.service.CredentialConfigService;
import com.SteamGame.login.dto.ApiResponse;
import com.SteamGame.login.dto.ResultCode;
import com.SteamGame.login.dto.CredentialCheckResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import com.SteamGame.login.dto.CredentialInputDTO;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import com.SteamGame.util.CryptoUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class CredentialConfigServiceImpl implements CredentialConfigService {

    private static final Logger logger = LoggerFactory.getLogger(CredentialConfigServiceImpl.class);

    @Value("${login.config.path:auth.yaml}")
    private String configPath;

    @Value("${login.encryption.base64Key:}")
    private String base64Key;

    private static final DateTimeFormatter TF = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    @Override
    public ApiResponse<Object> saveCredentialInfo(CredentialInputDTO loginDTO) {
        return saveCredentialWithValidation(loginDTO, null);
    }

    @Override
    public ApiResponse<Object> saveCredentialWithValidation(CredentialInputDTO loginDTO, CredentialCheckResult validationResult) {
        try {
            String now = LocalDateTime.now(ZoneOffset.UTC).format(TF);
            String steamId = loginDTO.getSteamId() == null ? null : loginDTO.getSteamId().trim();
            String apiKey = loginDTO.getApiKey() == null ? null : loginDTO.getApiKey().trim();

            if (apiKey == null || apiKey.isEmpty()) {
                logger.warn("尝试保存空的 apiKey，拒绝保存");
                return ApiResponse.fail(ResultCode.CONFIG_INVALID, "apiKey 不能为空");
            }

            if (base64Key == null || base64Key.isEmpty()) {
                logger.error("未配置加密密钥 login.encryption.base64Key，无法保存凭据（禁止明文存储）");
                return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "服务器未配置加密密钥，无法保存凭据");
            }

            // encrypt apiKey
            CryptoUtil.EncryptResult enc = CryptoUtil.encrypt(apiKey, base64Key);

            // build auth map
            Map<String, Object> auth = new LinkedHashMap<>();
            auth.put("steamId", steamId);
            auth.put("apiKeyEncrypted", enc.cipherTextBase64);

            Map<String, Object> keyMeta = new LinkedHashMap<>();
            keyMeta.put("algorithm", com.SteamGame.constant.SecurityConstants.CIPHER_TRANSFORMATION);
            keyMeta.put("iv", enc.ivBase64);
            auth.put("keyMeta", keyMeta);

            // validation metadata
            Map<String, Object> valMeta = new LinkedHashMap<>();
            if (validationResult != null) {
                valMeta.put("status", validationResult.isValidKeyAndUser() ? "VALID" : "INVALID");
                valMeta.put("lastValidatedAt", now);
                valMeta.put("nextRevalidateAt",
                        LocalDateTime.now(ZoneOffset.UTC).plusHours(6).format(TF));
                valMeta.put("failCount", validationResult.isValidKeyAndUser() ? 0 : 1);
                valMeta.put("lastErrorCode", validationResult.isValidKeyAndUser() ? "" : "INVALID_KEY_OR_USER");
            } else {
                valMeta.put("status", "UNKNOWN");
                valMeta.put("lastValidatedAt", now);
                valMeta.put("nextRevalidateAt",
                        LocalDateTime.now(ZoneOffset.UTC).plusHours(6).format(TF));
                valMeta.put("failCount", 0);
                valMeta.put("lastErrorCode", "");
            }
            auth.put("validation", valMeta);

            auth.put("version", com.SteamGame.constant.SecurityConstants.AUTH_CONFIG_VERSION);
            auth.put("updatedAt", now);

            Map<String, Object> rootMap = new LinkedHashMap<>();
            rootMap.put("auth", auth);

            // write atomically via temp file
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setPrettyFlow(true);
            Yaml yaml = new Yaml(options);

            File target = new File(configPath);
            File tmp = new File(configPath + ".tmp");
            try (FileWriter writer = new FileWriter(tmp)) {
                yaml.dump(rootMap, writer);
            }
            Files.move(tmp.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);

            logger.info("已保存加密凭据到 {} — SteamID: {}, updatedAt: {}", configPath, steamId, now);

            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("steamId", steamId);
            resp.put("updatedAt", now);
            resp.put("persisted", true);

            return ApiResponse.ok(ResultCode.REGISTER_OK, resp, "凭据配置保存成功");
        } catch (IOException e) {
            logger.error("保存凭据到 YAML 文件时发生 I/O 错误: {}", e.getMessage(), e);
            return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "写入配置文件失败: " + e.getMessage());
        } catch (Exception e) {
            logger.error("处理凭据信息时发生意外错误", e);
            return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "处理凭据时发生异常");
        }
    }
}
