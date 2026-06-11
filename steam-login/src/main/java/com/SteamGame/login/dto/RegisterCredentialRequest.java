package com.SteamGame.login.dto;

public class RegisterCredentialRequest {
    private String userId;
    private String steamId;
    private String apiKey;
    private boolean rememberMe;

    public RegisterCredentialRequest() {
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

    public boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }
}
