package com.SteamGame.util;

import com.SteamGame.login.dto.CredentialDTO;
import com.SteamGame.login.dto.CredentialInputDTO;

public class ValidationUtil {
    // 简单的用户名验证
    public static boolean isValidUsername(String username) {
        return username != null && username.length() >= 4 && username.length() <= 20;
    }

    // 简单的密码验证
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6 && password.length() <= 50;
    }

    public static boolean isValidLoginInfo(CredentialInputDTO loginDTO) {
        if (loginDTO == null)
            return false;
        if (loginDTO.getSteamId() == null || loginDTO.getApiKey() == null)
            return false;
        String apiKey = loginDTO.getApiKey();
        return isValidPassword(apiKey);
    }

    // 保留对旧 DTO 的兼容方法（不会泄露敏感信息）
    public static boolean isValidLoginInfo(CredentialDTO loginDTO) {
        if (loginDTO == null)
            return false;
        if (loginDTO.getSteamId() == null || loginDTO.getApiKey() == null)
            return false;
        String apiKey = loginDTO.getApiKey();
        return isValidPassword(apiKey);
    }
}
