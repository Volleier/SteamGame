package com.SteamGame.login.dto;

public class CredentialStatusView {
    private String userId;
    private String steamId;
    private String apiKeyMasked;
    private String validationStatus;
    private String lastValidatedAt;
    private String nextRevalidateAt;

    public CredentialStatusView() {
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

    public String getApiKeyMasked() {
        return apiKeyMasked;
    }

    public void setApiKeyMasked(String apiKeyMasked) {
        this.apiKeyMasked = apiKeyMasked;
    }

    public String getValidationStatus() {
        return validationStatus;
    }

    public void setValidationStatus(String validationStatus) {
        this.validationStatus = validationStatus;
    }

    public String getLastValidatedAt() {
        return lastValidatedAt;
    }

    public void setLastValidatedAt(String lastValidatedAt) {
        this.lastValidatedAt = lastValidatedAt;
    }

    public String getNextRevalidateAt() {
        return nextRevalidateAt;
    }

    public void setNextRevalidateAt(String nextRevalidateAt) {
        this.nextRevalidateAt = nextRevalidateAt;
    }
}
