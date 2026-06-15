package com.SteamGame.api.domain.player;

import java.sql.Timestamp;

/**
 * Domain entity for recent_game table.
 * Caches recently played games from IPlayerService/GetRecentlyPlayedGames.
 */
public class RecentGame {
    private Long id;
    private String userId;
    private Long appid;
    private String name;
    private Integer playtime2weeks;
    private Integer playtimeForever;
    private String iconUrl;
    private Timestamp syncedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Long getAppid() { return appid; }
    public void setAppid(Long appid) { this.appid = appid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getPlaytime2weeks() { return playtime2weeks; }
    public void setPlaytime2weeks(Integer playtime2weeks) { this.playtime2weeks = playtime2weeks; }

    public Integer getPlaytimeForever() { return playtimeForever; }
    public void setPlaytimeForever(Integer playtimeForever) { this.playtimeForever = playtimeForever; }

    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }

    public Timestamp getSyncedAt() { return syncedAt; }
    public void setSyncedAt(Timestamp syncedAt) { this.syncedAt = syncedAt; }
}
