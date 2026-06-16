package com.SteamGame.api.service;

import com.SteamGame.api.dto.GameListPageDTO;
import com.SteamGame.api.dto.GameListQuery;

public interface GameQueryService {
    GameListPageDTO queryGames(GameListQuery query);
}
