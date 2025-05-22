package com.SteamGame.login.controller;

import com.SteamGame.login.dto.LoginDTO;
import org.slf4j.Logger;
import com.SteamGame.login.service.RegisterService;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/register")
public class RegisterController {
    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);
    private final RegisterService registerService;

    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    /**
     * 用于初始化时调用的方法
     */
    public void register() {
        // 应用初始化时的登录逻辑，可以调用service
    }

    /**
     * 处理前端登录请求的API端点
     */
    @PostMapping
    public ResponseEntity<Boolean> handleLogin(@RequestBody LoginDTO loginDTO) {
        logger.info("接收到登录请求 - SteamID: {}, API Key: {}",
                loginDTO.getSteamId(), loginDTO.getApiKey());
        boolean result = registerService.
                saveLoginInfo(loginDTO);
        return ResponseEntity.ok(result);
    }
}