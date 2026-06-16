package com.SteamGame.api.domain.player;

import java.sql.Timestamp;

/**
 * Domain entity for player_wishlist table.
 * Caches Steam wishlist from IWishlistService/GetWishlist.
 */
public class PlayerWishlist {
    private Long id;
    private String userId;
    private Long appid;
    private String name;
    private Integer priority;
    private Long addedAt;
    private Timestamp syncedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Long getAppid() { return appid; }
    public void setAppid(Long appid) { this.appid = appid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }

    public Long getAddedAt() { return addedAt; }
    public void setAddedAt(Long addedAt) { this.addedAt = addedAt; }

    public Timestamp getSyncedAt() { return syncedAt; }
    public void setSyncedAt(Timestamp syncedAt) { this.syncedAt = syncedAt; }
}
