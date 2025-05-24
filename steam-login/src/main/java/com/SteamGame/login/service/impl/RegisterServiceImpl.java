package com.SteamGame.login.service.impl;

import com.SteamGame.login.dto.LoginDTO;
import com.SteamGame.login.service.RegisterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class RegisterServiceImpl implements RegisterService {

    private static final Logger logger = LoggerFactory.getLogger(RegisterServiceImpl.class);

    @Value("${login.config.path:auth.yaml}")
    private String configPath;

    @Override
    public boolean receiveLoginInfo(LoginDTO loginDTO) {
        return false;
    }

    @Override
    public boolean saveLoginInfo(LoginDTO loginDTO) {
        try {
            // 设置当前时间
            String currentTime = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            loginDTO.setTime(currentTime);

            // 创建YAML数据结构
            Map<String, Object> authData = new HashMap<>();
            authData.put("steamId", loginDTO.getSteamId());
            authData.put("apiKey", loginDTO.getApiKey());
            authData.put("rememberMe", loginDTO.isRememberMe());
            authData.put("time", loginDTO.getTime());

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