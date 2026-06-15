package com.SteamGame.api.domain.achievement;

import java.sql.Timestamp;

/**
 * Domain entity for game_achievement_global table.
 * Caches global achievement percentages from ISteamUserStats/GetGlobalAchievementPercentagesForApp.
 */
public class GameAchievementGlobal {
    private Long id;
    private Long appid;
    private String name;
    private Double percent;
    private Timestamp syncedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAppid() { return appid; }
    public void setAppid(Long appid) { this.appid = appid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getPercent() { return percent; }
    public void setPercent(Double percent) { this.percent = percent; }

    public Timestamp getSyncedAt() { return syncedAt; }
    public void setSyncedAt(Timestamp syncedAt) { this.syncedAt = syncedAt; }
}
