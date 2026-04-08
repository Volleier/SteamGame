package com.SteamGame.login.controller;

import com.SteamGame.login.dto.CredentialDTO;
import com.SteamGame.login.service.CredentialVerifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/credentials")
public class CredentialVerifyController {
    private static final Logger logger = LoggerFactory.getLogger(CredentialVerifyController.class);
    private final CredentialVerifyService loginService;

    @Autowired
    public CredentialVerifyController(CredentialVerifyService loginService) {
        this.loginService = loginService;
    }

    public void init() {
        // 应用初始化时的凭据相关初始化逻辑，可以调用 service
    }

    // 保留兼容方法，供启动时调用
    public void login() {
        init();
    }

    @GetMapping("/status")
    public ResponseEntity<CredentialDTO> getCredentialsStatus() {
        logger.info("发送凭据状态到前端");
        return loginService.sendCredentialInfoToFrontend();
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyCredentialsUsingLocalConfig() {
        logger.info("POST /api/credentials/verify - 使用本地 YAML 配置执行凭据验证");
        return loginService.validateCredential();
    }
}
