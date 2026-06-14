package com.SteamGame.common.error;

/**
 * 统一错误码枚举 —— 供所有模块共用。
 */
public enum ErrorCode {
    SUCCESS(200, "success"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_ERROR(500, "服务器内部错误"),
    STEAM_CREDENTIAL_NOT_FOUND(1001, "Steam 凭据未配置"),
    STEAM_CREDENTIAL_INVALID(1002, "Steam 凭据无效"),
    STEAM_API_TIMEOUT(2001, "Steam API 请求超时"),
    STEAM_API_UNAVAILABLE(2002, "Steam API 不可用"),
    GAME_SYNC_FAILED(3001, "游戏库同步失败");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
