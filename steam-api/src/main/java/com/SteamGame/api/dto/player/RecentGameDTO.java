package com.SteamGame.api.dto.player;

/**
 * DTO for a single recently played game.
 */
public class RecentGameDTO {
    private Long appid;
    private String name;
    private Integer playtime2Weeks;
    private Integer playtimeForever;
    private String iconUrl;

    public Long getAppid() { return appid; }
    public void setAppid(Long appid) { this.appid = appid; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getPlaytime2Weeks() { return playtime2Weeks; }
    public void setPlaytime2Weeks(Integer playtime2Weeks) { this.playtime2Weeks = playtime2Weeks; }
    public Integer getPlaytimeForever() { return playtimeForever; }
    public void setPlaytimeForever(Integer playtimeForever) { this.playtimeForever = playtimeForever; }
    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }
}
