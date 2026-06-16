package com.SteamGame.api.dto.metadata;

/**
 * DTO for batch metadata sync result (POST /api/game-metadata/sync-missing).
 */
public class GameMetadataSyncResultDTO {
    private int requested;
    private int updated;
    private int failed;

    public int getRequested() { return requested; }
    public void setRequested(int requested) { this.requested = requested; }
    public int getUpdated() { return updated; }
    public void setUpdated(int updated) { this.updated = updated; }
    public int getFailed() { return failed; }
    public void setFailed(int failed) { this.failed = failed; }
}
