package com.SteamGame.login.controller;

import com.SteamGame.login.dto.ApiResponse;
import com.SteamGame.login.dto.CredentialViewDTO;
import com.SteamGame.login.service.CredentialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/credentials")
public class CredentialVerifyController {

    private static final Logger logger = LoggerFactory.getLogger(CredentialVerifyController.class);

    private final CredentialService credentialService;

    @Autowired
    public CredentialVerifyController(CredentialService credentialService) {
        this.credentialService = credentialService;
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<CredentialViewDTO>> status(
            @RequestParam(value = "userId", required = false) String userId) {
        ApiResponse<CredentialViewDTO> resp = credentialService.getCredentialStatus(userId);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<?>> verify(@RequestParam(value = "userId", required = false) String userId) {
        logger.info("POST /api/credentials/verify - userId={}", userId);
        ApiResponse<?> resp = credentialService.loadAndValidateForLogin(userId);
        return ResponseEntity.ok(resp);
    }

    // 保留一个 init 方法供 legacy launcher 在启动时调用（不再读取 auth.yaml）
    public void init() {
        logger.info("CredentialVerifyController init() called");
    }
}
