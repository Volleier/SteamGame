package com.SteamGame.api.domain.metadata;

import java.sql.Timestamp;

/**
 * Domain entity for game_genre table.
 * Stores genres from Steam Store AppDetails.
 */
public class GameGenre {
    private Long id;
    private Long appid;
    private String genreId;
    private String description;
    private String source;
    private Timestamp updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAppid() { return appid; }
    public void setAppid(Long appid) { this.appid = appid; }

    public String getGenreId() { return genreId; }
    public void setGenreId(String genreId) { this.genreId = genreId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
