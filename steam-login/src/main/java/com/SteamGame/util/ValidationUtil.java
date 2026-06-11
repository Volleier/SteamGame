package com.SteamGame.util;

import com.SteamGame.login.dto.CredentialDTO;
import com.SteamGame.login.dto.CredentialInputDTO;
import com.SteamGame.login.dto.ResultCode;

/**
 * 输入格式校验工具 — 凭据配置与验证公用
 */
public class ValidationUtil {

    // SteamID64 should be 17-digit numeric string
    private static final String STEAM_ID_PATTERN = "^\\d{17}$";
    // Steam Web API Key is typically 32 hex characters
    private static final String API_KEY_PATTERN = "^[A-Fa-f0-9]{32}$";

    public static boolean isValidSteamId(String steamId) {
        return steamId != null && steamId.trim().matches(STEAM_ID_PATTERN);
    }

    public static boolean isValidApiKeyFormat(String apiKey) {
        return apiKey != null && apiKey.trim().matches(API_KEY_PATTERN);
    }

    /**
     * Validate steamId + apiKey format. Returns the error ResultCode, or null if valid.
     */
    public static ResultCode validateCredentialInput(String steamId, String apiKey) {
        if (steamId == null || steamId.trim().isEmpty()) {
            return ResultCode.INVALID_STEAM_ID;
        }
        if (!steamId.trim().matches(STEAM_ID_PATTERN)) {
            return ResultCode.INVALID_STEAM_ID;
        }
        if (apiKey == null || apiKey.trim().isEmpty()) {
            return ResultCode.INVALID_API_KEY_FORMAT;
        }
        if (!apiKey.trim().matches(API_KEY_PATTERN)) {
            return ResultCode.INVALID_API_KEY_FORMAT;
        }
        return null; // valid
    }

    // --- legacy compat ---
    public static boolean isValidUsername(String username) {
        return username != null && username.length() >= 4 && username.length() <= 20;
    }

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

    public static boolean isValidLoginInfo(CredentialDTO loginDTO) {
        if (loginDTO == null)
            return false;
        if (loginDTO.getSteamId() == null || loginDTO.getApiKey() == null)
            return false;
        String apiKey = loginDTO.getApiKey();
        return isValidPassword(apiKey);
    }
}
