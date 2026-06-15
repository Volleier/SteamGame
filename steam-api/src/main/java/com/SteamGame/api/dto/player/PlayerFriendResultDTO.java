package com.SteamGame.api.dto.player;

import java.util.List;

/**
 * DTO for friend list response (GET /api/player/friends).
 */
public class PlayerFriendResultDTO {
    private List<PlayerFriendDTO> items;

    public List<PlayerFriendDTO> getItems() { return items; }
    public void setItems(List<PlayerFriendDTO> items) { this.items = items; }
}
