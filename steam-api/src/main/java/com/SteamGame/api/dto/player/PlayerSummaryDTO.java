package com.SteamGame.api.dto.player;

import java.sql.Timestamp;

/**
 * DTO for player dashboard summary (GET /api/player/summary).
 */
public class PlayerSummaryDTO {
    private PlayerProfileDTO profile;
    private Integer ownedGameCount;
    private Integer recentGameCount;
    private Long totalPlaytimeForever;
    private Timestamp lastSyncedAt;

    public PlayerProfileDTO getProfile() { return profile; }
    public void setProfile(PlayerProfileDTO profile) { this.profile = profile; }
    public Integer getOwnedGameCount() { return ownedGameCount; }
    public void setOwnedGameCount(Integer ownedGameCount) { this.ownedGameCount = ownedGameCount; }
    public Integer getRecentGameCount() { return recentGameCount; }
    public void setRecentGameCount(Integer recentGameCount) { this.recentGameCount = recentGameCount; }
    public Long getTotalPlaytimeForever() { return totalPlaytimeForever; }
    public void setTotalPlaytimeForever(Long totalPlaytimeForever) { this.totalPlaytimeForever = totalPlaytimeForever; }
    public Timestamp getLastSyncedAt() { return lastSyncedAt; }
    public void setLastSyncedAt(Timestamp lastSyncedAt) { this.lastSyncedAt = lastSyncedAt; }
}
