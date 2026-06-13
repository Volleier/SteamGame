package com.SteamGame.api.dto;

public class OwnedGameCountDTO {
    private int count;

    public OwnedGameCountDTO() {
    }

    public OwnedGameCountDTO(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
