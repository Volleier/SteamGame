package com.SteamGame.login.model;

public class CredentialRecord {
    private String userId;
    private String steamId;
    private String apiKey; // may be encrypted by service layer
    private CredentialValidationMeta validation;

    public CredentialRecord() {
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

    public CredentialValidationMeta getValidation() {
        return validation;
    }

    public void setValidation(CredentialValidationMeta validation) {
        this.validation = validation;
    }
}
