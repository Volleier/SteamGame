package com.SteamGame.api.domain.stats;

import java.sql.Timestamp;

/**
 * Domain entity for game_realtime_stats table.
 * Caches current player count from Steam ISteamUserStats/GetNumberOfCurrentPlayers.
 */
public class GameRealtimeStats {
    private Long appid;
    private Integer playerCount;
    private Boolean cached;
    private Boolean stale;
    private Timestamp syncedAt;
    private Timestamp updatedAt;

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

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
