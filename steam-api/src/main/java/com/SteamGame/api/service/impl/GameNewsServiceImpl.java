package com.SteamGame.api.service.impl;

import com.SteamGame.api.client.steam.SteamWebApiClient;
import com.SteamGame.api.domain.news.GameNews;
import com.SteamGame.api.dto.news.GameNewsResultDTO;
import com.SteamGame.api.mapper.GameNewsMapper;
import com.SteamGame.api.service.GameNewsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class GameNewsServiceImpl implements GameNewsService {

    private static final Logger log = LoggerFactory.getLogger(GameNewsServiceImpl.class);

    private final SteamWebApiClient webApiClient;
    private final GameNewsMapper newsMapper;

    public GameNewsServiceImpl(SteamWebApiClient webApiClient, GameNewsMapper newsMapper) {
        this.webApiClient = webApiClient;
        this.newsMapper = newsMapper;
    }

    @Override
    public GameNewsResultDTO getNews(Long appid, int count, Integer maxLength) {
        try {
            GameNewsResultDTO result = webApiClient.getNewsForApp(appid, count, maxLength);
            if (result != null && result.getItems() != null) {
                Timestamp now = new Timestamp(System.currentTimeMillis());
                for (var item : result.getItems()) {
                    GameNews news = new GameNews();
                    news.setGid(item.getGid());
                    news.setAppid(appid);
                    news.setTitle(item.getTitle());
                    news.setUrl(item.getUrl());
                    news.setExternalUrl(item.getIsExternalUrl());
                    news.setAuthor(item.getAuthor());
                    news.setContents(item.getContents());
                    news.setFeedLabel(item.getFeedLabel());
                    news.setDate(item.getDate());
                    news.setSyncedAt(now);
                    newsMapper.upsert(news);
                }
            }
            return result;
        } catch (Exception e) {
            log.warn("Failed to fetch news for appid={}: {}", appid, e.getMessage());
            GameNewsResultDTO fallback = new GameNewsResultDTO();
            fallback.setAppid(appid);
            fallback.setItems(java.util.List.of());
            return fallback;
        }
    }
}
