package com.SteamGame.api.service;

import com.SteamGame.api.dto.player.WishlistResultDTO;

public interface PlayerWishlistService {
    WishlistResultDTO getWishlist(String userId);
}
