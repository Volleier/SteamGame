package com.SteamGame.login.controller;

import com.SteamGame.login.dto.LoginDTO;
import com.SteamGame.login.service.RegisterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 注册相关接口控制器
 */
@RestController
@RequestMapping("/api")
public class RegisterController {

    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);

    private final RegisterService registerService;

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
    public ResponseEntity<String> register(@RequestBody LoginDTO loginDTO) {
        logger.info("收到注册请求 - SteamID: {}", loginDTO.getSteamId());

        // 校验必要字段
        if (loginDTO.getSteamId() == null || loginDTO.getApiKey() == null) {
            logger.warn("注册信息不完整，缺少必要字段");
            return ResponseEntity.badRequest().body("steamId 和 apiKey 为必填项");
        }

        // 保存登录信息
        boolean saved = registerService.saveLoginInfo(loginDTO);

        if (saved) {
            logger.info("注册信息保存成功");
            return ResponseEntity.ok("注册成功");
        } else {
            logger.error("保存注册信息失败");
            return ResponseEntity.internalServerError().body("注册失败，请稍后重试");
        }
    }
}