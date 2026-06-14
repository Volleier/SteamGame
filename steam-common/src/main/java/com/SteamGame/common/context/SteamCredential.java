package com.SteamGame.common.context;

/**
 * 解密后的 Steam 凭证，供后端内部使用，不暴露给前端。
 */
public class SteamCredential {
    private String userId;
    private String steamId;
    private String apiKey;

    public SteamCredential() {
    }

    public SteamCredential(String userId, String steamId, String apiKey) {
        this.userId = userId;
        this.steamId = steamId;
        this.apiKey = apiKey;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSteamId() {
        return steamId;
    }

    public void setSteamId(String steamId) {
        this.steamId = steamId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public boolean isValid() {
        return steamId != null && !steamId.isEmpty()
                && apiKey != null && !apiKey.isEmpty();
    }
}
