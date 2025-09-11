package com.SteamGame.login.dto;

public class LoginCheckResult {
    private boolean validKeyAndUser;
    private boolean ownsGames;
    private boolean profilePrivate;
    private String message;
    private Integer gameCount;

    public LoginCheckResult() {
    }

    public LoginCheckResult(boolean validKeyAndUser, boolean ownsGames, boolean profilePrivate, String message, Integer gameCount) {
        this.validKeyAndUser = validKeyAndUser;
        this.ownsGames = ownsGames;
        this.profilePrivate = profilePrivate;
        this.message = message;
        this.gameCount = gameCount;
    }

    public boolean isValidKeyAndUser() {
        return validKeyAndUser;
    }

    public void setValidKeyAndUser(boolean validKeyAndUser) {
        this.validKeyAndUser = validKeyAndUser;
    }

    public boolean isOwnsGames() {
        return ownsGames;
    }

    public void setOwnsGames(boolean ownsGames) {
        this.ownsGames = ownsGames;
    }

    public boolean isProfilePrivate() {
        return profilePrivate;
    }

    public void setProfilePrivate(boolean profilePrivate) {
        this.profilePrivate = profilePrivate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getGameCount() {
        return gameCount;
    }

    public void setGameCount(Integer gameCount) {
        this.gameCount = gameCount;
    }
}
