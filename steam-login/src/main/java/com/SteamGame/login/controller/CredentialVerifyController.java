package com.SteamGame.login.controller;

import com.SteamGame.login.dto.ApiResponse;
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
    private final com.SteamGame.login.service.CredentialVerifyService loginService;
    private final com.SteamGame.login.service.CredentialService credentialService;

    @Autowired
    public CredentialVerifyController(CredentialVerifyService loginService,
            com.SteamGame.login.service.CredentialService credentialService) {
        this.loginService = loginService;
        this.credentialService = credentialService;
    }

    public void init() {
        // 应用初始化时的凭据相关初始化逻辑，可以调用 service
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<?>> getCredentialsStatus() {
        logger.info("发送凭据状态到前端");
        ApiResponse<?> resp = loginService.sendCredentialInfoToFrontend();
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Object>> verifyCredentialsUsingLocalConfig(
            @org.springframework.web.bind.annotation.RequestParam(value = "userId", required = false) String userId) {
        logger.info("POST /api/credentials/verify - 使用本地 YAML 配置执行凭据验证 (userId={})", userId);
        // 若提供 userId，则尝试基于该 userId 加载并校验；否则使用默认行为
        ApiResponse<com.SteamGame.login.dto.CredentialCheckResult> resp = credentialService
                .loadAndValidateForLogin(userId);
        if (resp == null) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.fail(com.SteamGame.login.dto.ResultCode.INTERNAL_ERROR, "验证服务内部错误"));
        }
        return ResponseEntity.ok((ApiResponse<Object>) resp);
    }
}
