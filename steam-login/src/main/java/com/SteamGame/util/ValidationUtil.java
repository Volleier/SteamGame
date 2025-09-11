package com.SteamGame.util;

import com.SteamGame.login.dto.LoginDTO;

public class ValidationUtil {
    // 简单的用户名验证
    public static boolean isValidUsername(String username) {
        return username != null && username.length() >= 4 && username.length() <= 20;
    }

    // 简单的密码验证
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6 && password.length() <= 20;
    }

    public static boolean isValidLoginInfo(LoginDTO loginDTO) {
    if (loginDTO == null) return false;
    // require steamId and apiKey present
    if (loginDTO.getSteamId() == null || loginDTO.getApiKey() == null) return false;
    // 这里复用密码校验规则来校验 apiKey 的格式长度（如果你有更具体的 apiKey 校验规则，可在此替换）
    String apiKey = loginDTO.getApiKey();
    return isValidPassword(apiKey);
    }
}
