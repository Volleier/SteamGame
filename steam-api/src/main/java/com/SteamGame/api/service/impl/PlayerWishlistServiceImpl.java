package com.SteamGame.api.service.impl;

import com.SteamGame.api.dto.player.WishlistItemDTO;
import com.SteamGame.api.dto.player.WishlistResultDTO;
import com.SteamGame.api.service.PlayerWishlistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Wishlist is a P3 feature. Steam's IWishlistService may require special permissions.
 * This implementation returns an empty list gracefully rather than throwing errors.
 */
@Service
public class PlayerWishlistServiceImpl implements PlayerWishlistService {

    private static final Logger log = LoggerFactory.getLogger(PlayerWishlistServiceImpl.class);

    @Override
    public WishlistResultDTO getWishlist(String userId) {
        log.info("Wishlist requested for userId={} — returning empty (P3 feature, not yet fully implemented)", userId);
        WishlistResultDTO result = new WishlistResultDTO();
        result.setItems(List.of());
        return result;
    }
}
