package com.SteamGame.login.model;

public class CredentialValidationMeta {
    private String status; // VALID / INVALID / UNKNOWN
    private String lastValidatedAt;
    private String nextRevalidateAt;
    private int failCount;
    private String lastErrorCode;

    public CredentialValidationMeta() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public int getFailCount() {
        return failCount;
    }

    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }

    public String getLastErrorCode() {
        return lastErrorCode;
    }

    public void setLastErrorCode(String lastErrorCode) {
        this.lastErrorCode = lastErrorCode;
    }
}
