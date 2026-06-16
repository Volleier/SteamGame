package com.SteamGame.api.service;

import com.SteamGame.api.dto.news.GameNewsResultDTO;

public interface GameNewsService {
    GameNewsResultDTO getNews(Long appid, int count, Integer maxLength);
}
