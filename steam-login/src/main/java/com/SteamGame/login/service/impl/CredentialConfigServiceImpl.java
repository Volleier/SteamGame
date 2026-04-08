package com.SteamGame.login.service.impl;

import com.SteamGame.login.service.CredentialConfigService;
import com.SteamGame.login.dto.ApiResponse;
import com.SteamGame.login.dto.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import com.SteamGame.login.dto.CredentialInputDTO;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import com.SteamGame.util.CryptoUtil;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class CredentialConfigServiceImpl implements CredentialConfigService {

    private static final Logger logger = LoggerFactory.getLogger(CredentialConfigServiceImpl.class);

    @Value("${login.config.path:auth.yaml}")
    private String configPath;

    @Value("${login.encryption.base64Key:}")
    private String base64Key;

    @Override
    public ApiResponse<Object> saveCredentialInfo(CredentialInputDTO loginDTO) {
        try {
            // 设置当前时间
            String currentTime = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            // Construct internal DTO for time handling
            loginDTO.setApiKey(loginDTO.getApiKey());
            String timeToSet = currentTime;

            // 创建YAML数据结构
            Map<String, Object> authData = new HashMap<>();
            // 去除可能的前后空白
            String steamId = loginDTO.getSteamId() == null ? null : loginDTO.getSteamId().trim();
            String apiKey = loginDTO.getApiKey() == null ? null : loginDTO.getApiKey().trim();
            String time = timeToSet;

            if (apiKey == null || apiKey.isEmpty()) {
                logger.warn("尝试保存空的 apiKey，拒绝保存");
                return ApiResponse.fail(ResultCode.CONFIG_INVALID, "apiKey 不能为空");
            }

            if (base64Key == null || base64Key.isEmpty()) {
                logger.error("未配置加密密钥 login.encryption.base64Key，无法保存凭据（禁止明文存储）");
                return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "服务器未配置加密密钥，无法保存凭据");
            }

            // 使用 CryptoUtil 加密 apiKey
            CryptoUtil.EncryptResult enc = CryptoUtil.encrypt(apiKey, base64Key);

            authData.put("steamId", steamId);
            authData.put("apiKeyEncrypted", enc.cipherTextBase64);
            Map<String, Object> keyMeta = new HashMap<>();
            keyMeta.put(com.SteamGame.constant.SecurityConstants.AUTH_KEYMETA_ALGORITHM,
                    com.SteamGame.constant.SecurityConstants.CIPHER_TRANSFORMATION);
            keyMeta.put(com.SteamGame.constant.SecurityConstants.AUTH_KEYMETA_IV, enc.ivBase64);
            authData.put("keyMeta", keyMeta);
            authData.put("version", com.SteamGame.constant.SecurityConstants.AUTH_CONFIG_VERSION);
            authData.put("updatedAt", time);

            Map<String, Object> rootMap = new HashMap<>();
            rootMap.put("auth", authData);

            // 配置YAML输出格式
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setPrettyFlow(true);

            // 写入YAML文件
            Yaml yaml = new Yaml(options);
            try (FileWriter writer = new FileWriter(configPath)) {
                yaml.dump(rootMap, writer);
            }

            logger.info("已成功保存登录信息到YAML - SteamID: {}, 保存时间: {}",
                    loginDTO.getSteamId(), currentTime);

            Map<String, Object> resp = new HashMap<>();
            resp.put("steamId", steamId);
            resp.put("updatedAt", time);

            return ApiResponse.ok(ResultCode.REGISTER_OK, resp, "凭据配置保存成功");
        } catch (IOException e) {
            logger.error("保存登录信息到YAML文件时发生错误: {}", e.getMessage(), e);
            return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "写入配置文件失败: " + e.getMessage());
        } catch (Exception e) {
            logger.error("处理登录信息时发生意外错误", e);
            return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "处理凭据时发生异常");
        }
    }
}