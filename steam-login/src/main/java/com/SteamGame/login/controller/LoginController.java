package com.SteamGame.login.controller;

import com.SteamGame.login.dto.LoginDTO;
import com.SteamGame.login.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/login")
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private final LoginService loginService;

    /**
     * 构造方法注入RegisterService
     *
     * @param loginService 登录服务
     */
    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    public void login() {
        // 应用初始化时的登录逻辑，可以调用service
    }

    /**
     * 从YAML读取配置并发送到前端的API端点
     */
    @GetMapping
    public ResponseEntity<LoginDTO> getLoginInfo() {
        logger.info("sending login info to frontend");
        return loginService.sendLoginInfoToFrontend();
    }

    @PostMapping
    public ResponseEntity<?> loginUsingLocalConfig() {
        logger.info("POST /api/login - 使用本地 YAML 配置执行登录验证（合并 validate 到 login）");
        // 不接受前端请求体，始终使用本地 YAML 中的 steamId/apiKey 去验证
        return loginService.validateLogin();
    }
}