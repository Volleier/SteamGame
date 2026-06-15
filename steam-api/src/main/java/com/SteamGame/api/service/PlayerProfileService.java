package com.SteamGame.api.service;

import com.SteamGame.api.dto.player.PlayerProfileDTO;
import com.SteamGame.api.dto.player.PlayerSummaryDTO;

public interface PlayerProfileService {

    /**
     * Fetch player profile from Steam or local cache.
     */
    PlayerProfileDTO getProfile(String userId);

    /**
     * Return aggregated player summary for dashboard.
     */
    PlayerSummaryDTO getSummary(String userId);
}
