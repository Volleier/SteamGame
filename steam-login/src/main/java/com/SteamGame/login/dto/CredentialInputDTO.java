package com.SteamGame.login.dto;

public class CredentialInputDTO {
    private String steamId;
    private String apiKey;

    public CredentialInputDTO() {
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
}
