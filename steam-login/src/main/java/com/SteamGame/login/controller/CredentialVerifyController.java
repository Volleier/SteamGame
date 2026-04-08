package com.SteamGame.login.controller;

import com.SteamGame.login.dto.CredentialDTO;
import com.SteamGame.login.dto.ApiResponse;
import com.SteamGame.login.dto.ResultCode;
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
    public ResponseEntity<ApiResponse<CredentialDTO>> getCredentialsStatus() {
        logger.info("发送凭据状态到前端");
        ResponseEntity<CredentialDTO> resp = loginService.sendCredentialInfoToFrontend();
        if (resp == null || resp.getBody() == null) {
            return ResponseEntity.ok(ApiResponse.fail(ResultCode.CONFIG_NOT_FOUND, "未找到凭据配置"));
        }
        return ResponseEntity.ok(ApiResponse.ok(ResultCode.LOGIN_OK, resp.getBody(), "凭据状态已返回"));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Object>> verifyCredentialsUsingLocalConfig() {
        logger.info("POST /api/credentials/verify - 使用本地 YAML 配置执行凭据验证");
        ResponseEntity<?> svcResp = loginService.validateCredential();
        if (svcResp == null) {
            return ResponseEntity.status(500).body(ApiResponse.fail(ResultCode.INTERNAL_ERROR, "验证服务内部错误"));
        }

        Object body = svcResp.getBody();
        int status = svcResp.getStatusCodeValue();

        if (status >= 200 && status < 300) {
            return ResponseEntity.ok(ApiResponse.ok(ResultCode.LOGIN_OK, body, "凭据验证成功"));
        } else if (status >= 400 && status < 500) {
            return ResponseEntity.status(status).body(ApiResponse.fail(ResultCode.CONFIG_INVALID, "凭据验证失败"));
        } else {
            return ResponseEntity.status(status).body(ApiResponse.fail(ResultCode.INTERNAL_ERROR, "凭据验证过程中发生错误"));
        }
    }
}
