package com.SteamGame.api.dto;

/**
 * DTO for a single game in the paged game list (GET /api/games).
 */
public class GameListItemDTO {
    private Long appid;
    private String name;
    private Integer playtimeForever;
    private String developer;
    private String publisher;
    private String releaseDate;
    private String tags;
    private String headerImage;
    private Integer playerCount;

    public Long getAppid() { return appid; }
    public void setAppid(Long appid) { this.appid = appid; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getPlaytimeForever() { return playtimeForever; }
    public void setPlaytimeForever(Integer playtimeForever) { this.playtimeForever = playtimeForever; }
    public String getDeveloper() { return developer; }
    public void setDeveloper(String developer) { this.developer = developer; }
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public String getReleaseDate() { return releaseDate; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public String getHeaderImage() { return headerImage; }
    public void setHeaderImage(String headerImage) { this.headerImage = headerImage; }
    public Integer getPlayerCount() { return playerCount; }
    public void setPlayerCount(Integer playerCount) { this.playerCount = playerCount; }
}
