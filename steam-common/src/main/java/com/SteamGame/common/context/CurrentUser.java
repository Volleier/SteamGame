package com.SteamGame.common.context;

/**
 * 当前用户上下文信息。
 */
public class CurrentUser {
    private String userId;
    private String username;
    private boolean admin;

    public CurrentUser() {
    }

    public CurrentUser(String userId, String username, boolean admin) {
        this.userId = userId;
        this.username = username;
        this.admin = admin;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
