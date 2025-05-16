package com.SteamGame.login.controller;

import org.slf4j.Logger;
import com.SteamGame.login.dto.LoginDTO;
import com.SteamGame.login.service.LoginService;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    /**
     * 用于初始化时调用的方法
     */
    public void login() {
        // 应用初始化时的登录逻辑，可以调用service
        loginService.sayHello();
    }

    /**
     * 处理前端登录请求的API端点
     */
    @PostMapping
    public ResponseEntity<Boolean> handleLogin(@RequestBody LoginDTO loginDTO) {
        logger.info("接收到登录请求 - SteamID: {}, API Key: {}",
                loginDTO.getSteamId(), loginDTO.getApiKey());
        boolean result = loginService.saveLoginInfo(loginDTO);
        return ResponseEntity.ok(result);
    }
}