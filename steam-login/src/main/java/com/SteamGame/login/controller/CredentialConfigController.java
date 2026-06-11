package com.SteamGame.login.controller;

import com.SteamGame.login.dto.ApiResponse;
import com.SteamGame.login.dto.RegisterCredentialRequest;
import com.SteamGame.login.service.CredentialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/credentials")
public class CredentialConfigController {

    private static final Logger logger = LoggerFactory.getLogger(CredentialConfigController.class);

    private final CredentialService credentialService;

    @Autowired
    public CredentialConfigController(CredentialService credentialService) {
        this.credentialService = credentialService;
    }

    @PostMapping("/configure")
    public ResponseEntity<ApiResponse<?>> configure(
            @RequestBody com.SteamGame.login.dto.RegisterCredentialRequest req) {
        logger.info("POST /api/credentials/configure - userId={}", req.getUserId());
        ApiResponse<?> resp = credentialService.registerAndValidate(req);
        return ResponseEntity.ok(resp);
    }

    // 保留一个 init 方法供 legacy launcher 在启动时调用（不再读取 auth.yaml）
    public void init() {
        logger.info("CredentialConfigController init() called");
    }
}
