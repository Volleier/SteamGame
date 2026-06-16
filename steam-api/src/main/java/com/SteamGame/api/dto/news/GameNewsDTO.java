package com.SteamGame.api.dto.news;

/**
 * DTO for a single news item.
 */
public class GameNewsDTO {
    private String gid;
    private String title;
    private String url;
    private Boolean isExternalUrl;
    private String author;
    private String contents;
    private String feedLabel;
    private Long date;

    public String getGid() { return gid; }
    public void setGid(String gid) { this.gid = gid; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public Boolean getIsExternalUrl() { return isExternalUrl; }
    public void setIsExternalUrl(Boolean isExternalUrl) { this.isExternalUrl = isExternalUrl; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getContents() { return contents; }
    public void setContents(String contents) { this.contents = contents; }
    public String getFeedLabel() { return feedLabel; }
    public void setFeedLabel(String feedLabel) { this.feedLabel = feedLabel; }
    public Long getDate() { return date; }
    public void setDate(Long date) { this.date = date; }
}
