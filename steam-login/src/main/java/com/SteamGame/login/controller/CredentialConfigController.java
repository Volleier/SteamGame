package com.SteamGame.login.controller;

import com.SteamGame.login.dto.CredentialDTO;
import com.SteamGame.login.service.CredentialConfigService;
import com.SteamGame.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 凭据配置相关接口控制器（原 RegisterController）
 */
@RestController
@RequestMapping("/api/credentials")
public class CredentialConfigController {

    private static final Logger logger = LoggerFactory.getLogger(CredentialConfigController.class);

    private final CredentialConfigService registerService;

    // SteamApiService 可选，暂不直接依赖；若需要在线校验可通过 RestTemplate 实现

    @Autowired
    public CredentialConfigController(CredentialConfigService registerService) {
        this.registerService = registerService;
    }

    public void register() {
        // 应用初始化时的凭据配置初始化逻辑，可调用 service
    }

    @PostMapping("/configure")
    public ResponseEntity<Object> configureCredentials(@RequestBody CredentialDTO loginDTO) {
        logger.info("收到凭据配置请求 - SteamID: {}", loginDTO.getSteamId());

        if (loginDTO.getSteamId() == null || loginDTO.getApiKey() == null) {
            logger.warn("凭据配置信息不完整，缺少必要字段");
            return ResponseEntity.badRequest().body(Map.of("message", "steamId 和 apiKey 为必填项"));
        }

        if (!ValidationUtil.isValidPassword(loginDTO.getApiKey())) {
            logger.warn("凭据 apiKey 校验失败");
            return ResponseEntity.badRequest().body(Map.of("message", "apiKey 格式不符合要求（6-20 位）"));
        }

        // 在线校验已移除；保存凭据前可在部署环境中启用更严格的校验流程

        boolean saved = registerService.saveCredentialInfo(loginDTO);

        if (saved) {
            logger.info("凭据配置保存成功");
            return ResponseEntity.status(201).body(Map.of("message", "凭据配置保存成功", "steamId", loginDTO.getSteamId()));
        } else {
            logger.error("保存凭据信息失败");
            return ResponseEntity.status(500).body(Map.of("message", "凭据配置保存失败，请稍后重试"));
        }
    }
}
