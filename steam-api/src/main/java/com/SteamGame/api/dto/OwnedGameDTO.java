package com.SteamGame.api.dto;

public class OwnedGameDTO {
    private Long appid;
    private String name;
    private Integer playtimeForever;
    private String developer;
    private String publisher;
    private String releaseDate;
    private String tags;
    private Integer playtime2Weeks;
    private Long rtimeLastPlayed;
    private String iconUrl;
    private Boolean hasCommunityVisibleStats;
    private Integer playtimeWindowsForever;
    private Integer playtimeMacForever;
    private Integer playtimeLinuxForever;
    private Integer playtimeDeckForever;

    public OwnedGameDTO() {
    }

    public OwnedGameDTO(Long appid, String name, Integer playtimeForever,
            String developer, String publisher, String releaseDate, String tags) {
        this.appid = appid;
        this.name = name;
        this.playtimeForever = playtimeForever;
        this.developer = developer;
        this.publisher = publisher;
        this.releaseDate = releaseDate;
        this.tags = tags;
    }

    public Long getAppid() {
        return appid;
    }

    public void setAppid(Long appid) {
        this.appid = appid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPlaytimeForever() {
        return playtimeForever;
    }

    public void setPlaytimeForever(Integer playtimeForever) {
        this.playtimeForever = playtimeForever;
    }

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Integer getPlaytime2Weeks() { return playtime2Weeks; }
    public void setPlaytime2Weeks(Integer playtime2Weeks) { this.playtime2Weeks = playtime2Weeks; }

    public Long getRtimeLastPlayed() { return rtimeLastPlayed; }
    public void setRtimeLastPlayed(Long rtimeLastPlayed) { this.rtimeLastPlayed = rtimeLastPlayed; }

    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }

    public Boolean getHasCommunityVisibleStats() { return hasCommunityVisibleStats; }
    public void setHasCommunityVisibleStats(Boolean hasCommunityVisibleStats) { this.hasCommunityVisibleStats = hasCommunityVisibleStats; }

    public Integer getPlaytimeWindowsForever() { return playtimeWindowsForever; }
    public void setPlaytimeWindowsForever(Integer playtimeWindowsForever) { this.playtimeWindowsForever = playtimeWindowsForever; }

    public Integer getPlaytimeMacForever() { return playtimeMacForever; }
    public void setPlaytimeMacForever(Integer playtimeMacForever) { this.playtimeMacForever = playtimeMacForever; }

    public Integer getPlaytimeLinuxForever() { return playtimeLinuxForever; }
    public void setPlaytimeLinuxForever(Integer playtimeLinuxForever) { this.playtimeLinuxForever = playtimeLinuxForever; }

    public Integer getPlaytimeDeckForever() { return playtimeDeckForever; }
    public void setPlaytimeDeckForever(Integer playtimeDeckForever) { this.playtimeDeckForever = playtimeDeckForever; }
}
