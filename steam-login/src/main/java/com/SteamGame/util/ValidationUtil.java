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
        return true;
    }
}
