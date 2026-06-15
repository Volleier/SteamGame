package com.SteamGame.api.dto.player;

import java.util.List;

/**
 * DTO for wishlist response (GET /api/player/wishlist).
 */
public class WishlistResultDTO {
    private List<WishlistItemDTO> items;

    public List<WishlistItemDTO> getItems() { return items; }
    public void setItems(List<WishlistItemDTO> items) { this.items = items; }
}
