package com.SteamGame.login.dto;

/**
 * 凭据校验内部结果码 —— 仅用于 ValidationUtil 和 Service 内部校验逻辑。
 * 对外 API 响应统一使用 com.SteamGame.common.error.ErrorCode。
 */
public enum ResultCode {
    INVALID_STEAM_ID,
    INVALID_API_KEY_FORMAT
}
