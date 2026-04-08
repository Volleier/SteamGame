package com.SteamGame.constant;

public final class SecurityConstants {
    private SecurityConstants() {}

    // 加密相关
    public static final String CIPHER_ALGORITHM = "AES";
    public static final String CIPHER_TRANSFORMATION = "AES/GCM/NoPadding";
    public static final int GCM_IV_LENGTH = 12; // bytes
    public static final int GCM_TAG_LENGTH_BIT = 128;

    // 配置文件结构
    public static final String AUTH_ROOT = "auth";
    public static final String AUTH_APIKEY_FIELD = "apiKeyEncrypted";
    public static final String AUTH_KEYMETA_FIELD = "keyMeta";
    public static final String AUTH_KEYMETA_ALGORITHM = "algorithm";
    public static final String AUTH_KEYMETA_IV = "iv";
    public static final String AUTH_STEAMID_FIELD = "steamId";
    public static final String AUTH_UPDATED_AT = "updatedAt";
    public static final int AUTH_CONFIG_VERSION = 2;

    // 统一错误码键名（后端返回中使用）
    public static final String RESPONSE_CODE_KEY = "code";
}
// 额外常量：记住我相关
// 注意：如需可在此处扩展更多安全配置常量
public final class SecurityConstantsExtras {
    private SecurityConstantsExtras() {}
    public static final long REMEMBER_ME_EXPIRATION_TIME = 30L * 24 * 60 * 60 * 1000;
    public static final String REMEMBER_ME_KEY = "rememberMeKey";
}