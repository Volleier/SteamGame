package com.SteamGame.api.service;

import com.SteamGame.api.dto.stats.CurrentPlayerCountDTO;

import java.util.List;

public interface GameStatsService {
    CurrentPlayerCountDTO getCurrentPlayers(Long appid, boolean forceRefresh);
    List<CurrentPlayerCountDTO> getCurrentPlayersBatch(List<Long> appids, boolean forceRefresh);
}
