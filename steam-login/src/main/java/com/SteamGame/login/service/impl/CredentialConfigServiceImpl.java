package com.SteamGame.login.service.impl;

import com.SteamGame.login.dto.CredentialDTO;
import com.SteamGame.login.service.CredentialConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import com.SteamGame.util.CryptoUtil;
import org.springframework.beans.factory.annotation.Value;

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
    public boolean receiveCredentialInfo(CredentialDTO loginDTO) {
        return false;
    }

    @Override
    public boolean saveCredentialInfo(CredentialDTO loginDTO) {
        try {
            // 设置当前时间
            String currentTime = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            loginDTO.setTime(currentTime);

            // 创建YAML数据结构
            Map<String, Object> authData = new HashMap<>();
            // 去除可能的前后空白
            String steamId = loginDTO.getSteamId() == null ? null : loginDTO.getSteamId().trim();
            String apiKey = loginDTO.getApiKey() == null ? null : loginDTO.getApiKey().trim();
            String time = loginDTO.getTime() == null ? null : loginDTO.getTime().trim();

            if (apiKey == null || apiKey.isEmpty()) {
                logger.warn("尝试保存空的 apiKey，拒绝保存");
                return false;
            }

            if (base64Key == null || base64Key.isEmpty()) {
                logger.error("未配置加密密钥 login.encryption.base64Key，无法保存凭据（禁止明文存储）");
                return false;
            }

            // 使用 CryptoUtil 加密 apiKey
            CryptoUtil.EncryptResult enc = CryptoUtil.encrypt(apiKey, base64Key);

            authData.put("steamId", steamId);
            authData.put("apiKeyEncrypted", enc.cipherTextBase64);
            Map<String, Object> keyMeta = new HashMap<>();
            keyMeta.put("algorithm", "AES/GCM/NoPadding");
            keyMeta.put("iv", enc.ivBase64);
            authData.put("keyMeta", keyMeta);
            authData.put("version", 2);
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
            return true;
        } catch (IOException e) {
            logger.error("保存登录信息到YAML文件时发生错误: {}", e.getMessage(), e);
            return false;
        } catch (Exception e) {
            logger.error("处理登录信息时发生意外错误", e);
            return false;
        }
    }
}