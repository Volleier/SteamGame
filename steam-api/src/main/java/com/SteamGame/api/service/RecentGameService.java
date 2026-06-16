package com.SteamGame.api.service;

import com.SteamGame.api.dto.player.RecentGameResultDTO;

public interface RecentGameService {
    RecentGameResultDTO getRecentGames(String userId, int count);
}
