package com.SteamGame.api.dto.stats;

import java.sql.Timestamp;

/**
 * DTO for current player count query result.
 */
public class CurrentPlayerCountDTO {
    private Long appid;
    private Integer playerCount;
    private Boolean cached;
    private Boolean stale;
    private Timestamp syncedAt;

    public Long getAppid() { return appid; }
    public void setAppid(Long appid) { this.appid = appid; }
    public Integer getPlayerCount() { return playerCount; }
    public void setPlayerCount(Integer playerCount) { this.playerCount = playerCount; }
    public Boolean getCached() { return cached; }
    public void setCached(Boolean cached) { this.cached = cached; }
    public Boolean getStale() { return stale; }
    public void setStale(Boolean stale) { this.stale = stale; }
    public Timestamp getSyncedAt() { return syncedAt; }
    public void setSyncedAt(Timestamp syncedAt) { this.syncedAt = syncedAt; }
}
