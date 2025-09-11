package com.SteamGame.login.controller;

import com.SteamGame.login.dto.LoginDTO;
import com.SteamGame.login.service.RegisterService;
import com.SteamGame.util.ValidationUtil;
import com.SteamGame.api.service.SteamApiService;
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
 * 注册相关接口控制器
 */
@RestController
@RequestMapping("/api")
public class RegisterController {

    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);

    private final RegisterService registerService;

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private SteamApiService steamApiService;

    /**
     * 构造方法注入RegisterService
     *
     * @param registerService 注册服务
     */
    @Autowired
    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    public void register() {
        // 应用初始化时的登录逻辑，可以调用service
    }

    /**
     * 处理注册请求，保存登录信息
     *
     * @param loginDTO 登录信息DTO
     * @return 注册结果响应
     */
    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody LoginDTO loginDTO) {
        logger.info("收到注册请求 - SteamID: {}", loginDTO.getSteamId());

        // 校验必要字段
        if (loginDTO.getSteamId() == null || loginDTO.getApiKey() == null) {
            logger.warn("注册信息不完整，缺少必要字段");
            return ResponseEntity.badRequest().body(Map.of("message", "steamId 和 apiKey 为必填项"));
        }

        // 使用 ValidationUtil 验证 apiKey 格式（复用原有密码长度校验逻辑）
        if (!ValidationUtil.isValidPassword(loginDTO.getApiKey())) {
            logger.warn("注册 apiKey 校验失败");
            return ResponseEntity.badRequest().body(Map.of("message", "apiKey 格式不符合要求（6-20 位）"));
        }

        // 若 steam-api 模块可用，则验证 API key
        if (this.steamApiService != null) {
            try {
                boolean valid = steamApiService.isApiKeyValid(loginDTO.getApiKey());
                if (!valid) {
                    logger.warn("注册时 API key 无效");
                    return ResponseEntity.status(403).body(Map.of("message", "无效的 API key"));
                }
            } catch (Exception e) {
                logger.error("校验 API key 时出错", e);
                return ResponseEntity.status(502).body(Map.of("message", "验证 API key 时发生错误"));
            }
        } else {
            logger.info("SteamApiService 不可用，跳过 API key 在线校验");
        }

        // 保存登录信息
        boolean saved = registerService.saveLoginInfo(loginDTO);

        if (saved) {
            logger.info("注册信息保存成功");
            return ResponseEntity.status(201).body(Map.of("message", "注册成功", "steamId", loginDTO.getSteamId()));
        } else {
            logger.error("保存注册信息失败");
            return ResponseEntity.status(500).body(Map.of("message", "注册失败，请稍后重试"));
        }
    }
}