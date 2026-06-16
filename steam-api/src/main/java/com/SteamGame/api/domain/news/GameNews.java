package com.SteamGame.api.domain.news;

import java.sql.Timestamp;

/**
 * Domain entity for game_news table.
 * Caches game news from ISteamNews/GetNewsForApp.
 */
public class GameNews {
    private String gid;
    private Long appid;
    private String title;
    private String url;
    private Boolean externalUrl;
    private String author;
    private String contents;
    private String feedLabel;
    private Long date;
    private Timestamp syncedAt;

    public String getGid() { return gid; }
    public void setGid(String gid) { this.gid = gid; }

    public Long getAppid() { return appid; }
    public void setAppid(Long appid) { this.appid = appid; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public Boolean getExternalUrl() { return externalUrl; }
    public void setExternalUrl(Boolean externalUrl) { this.externalUrl = externalUrl; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getContents() { return contents; }
    public void setContents(String contents) { this.contents = contents; }

    public String getFeedLabel() { return feedLabel; }
    public void setFeedLabel(String feedLabel) { this.feedLabel = feedLabel; }

    public Long getDate() { return date; }
    public void setDate(Long date) { this.date = date; }

    public Timestamp getSyncedAt() { return syncedAt; }
    public void setSyncedAt(Timestamp syncedAt) { this.syncedAt = syncedAt; }
}
