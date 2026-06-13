package com.SteamGame.api.dto;

public class OwnedGameDTO {
    private Long appid;
    private String name;
    private Integer playtimeForever;
    private String developer;
    private String publisher;
    private String releaseDate;
    private String tags;

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
}
