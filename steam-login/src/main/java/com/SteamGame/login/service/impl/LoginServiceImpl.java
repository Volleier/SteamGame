package com.SteamGame.login.service.impl;

import com.SteamGame.login.dto.LoginDTO;
import com.SteamGame.login.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {

    private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

    @Value("${login.config.path:auth.yaml}")
    private String configPath;

    @Override
    public LoginDTO readLoginInfoFromYaml() {
        try {
            Yaml yaml = new Yaml();
            InputStream inputStream = new FileInputStream(configPath);
            Map<String, Object> loginData = yaml.load(inputStream);

            LoginDTO loginDTO = new LoginDTO();
            if (loginData != null && loginData.containsKey("auth")) {
                Map<String, Object> authData = (Map<String, Object>) loginData.get("auth");
                loginDTO.setSteamId((String) authData.get("steamId"));
                loginDTO.setApiKey((String) authData.get("apiKey"));
                loginDTO.setRememberMe((Boolean) authData.getOrDefault("rememberMe", false));
                loginDTO.setTime((String) authData.get("time"));
            }

            logger.info("从YAML读取的登录信息 - SteamID: {}, API Key: {}",
                    loginDTO.getSteamId(), loginDTO.getApiKey());

            return loginDTO;
        } catch (FileNotFoundException e) {
            logger.error("找不到登录配置文件: {}", configPath, e);
            return new LoginDTO();
        } catch (Exception e) {
            logger.error("读取登录配置文件时出错", e);
            return new LoginDTO();
        }
    }

    @Override
    public ResponseEntity<LoginDTO> sendLoginInfoToFrontend() {
        LoginDTO loginDTO = readLoginInfoFromYaml();
        logger.info("准备发送到前端的登录信息 - SteamID: {}, API Key: {}",
                loginDTO.getSteamId(), loginDTO.getApiKey());
        return ResponseEntity.ok(loginDTO);
    }
}
