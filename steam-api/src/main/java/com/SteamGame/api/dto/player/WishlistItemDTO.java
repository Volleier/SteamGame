package com.SteamGame.api.dto.player;

/**
 * DTO for a single wishlist item.
 */
public class WishlistItemDTO {
    private Long appid;
    private String name;
    private Integer priority;
    private Long addedAt;

    public Long getAppid() { return appid; }
    public void setAppid(Long appid) { this.appid = appid; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    public Long getAddedAt() { return addedAt; }
    public void setAddedAt(Long addedAt) { this.addedAt = addedAt; }
}
