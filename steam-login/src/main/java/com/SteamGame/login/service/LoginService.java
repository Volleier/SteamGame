package com.SteamGame.login.service;

import com.SteamGame.login.dto.LoginDTO;
import org.springframework.http.ResponseEntity;

public interface LoginService {
    /**
     * 从YAML配置文件读取登录信息
     *
     * @return LoginDTO 包含steamId和apiKey的登录信息
     */
    LoginDTO readLoginInfoFromYaml();

    /**
     * 将steamId和apiKey发送到前端
     *
     * @return 包含登录信息的ResponseEntity
     */
    ResponseEntity<LoginDTO> sendLoginInfoToFrontend();
}
