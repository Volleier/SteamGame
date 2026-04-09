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
    private final CredentialVerifyService loginService;

    @Autowired
    public CredentialVerifyController(CredentialVerifyService loginService) {
        this.loginService = loginService;
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
    public ResponseEntity<ApiResponse<Object>> verifyCredentialsUsingLocalConfig() {
        logger.info("POST /api/credentials/verify - 使用本地 YAML 配置执行凭据验证");
        ApiResponse<?> svcResp = loginService.validateCredential();
        if (svcResp == null) {
            return ResponseEntity.status(500)
                    .body(ApiResponse.fail(com.SteamGame.login.dto.ResultCode.INTERNAL_ERROR, "验证服务内部错误"));
        }
        // 控制器不做业务分支，直接转发统一 ApiResponse（客户端通过 code 判断行为）
        return ResponseEntity.ok((ApiResponse<Object>) svcResp);
    }
}
