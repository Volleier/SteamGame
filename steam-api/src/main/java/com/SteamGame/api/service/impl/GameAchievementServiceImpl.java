package com.SteamGame.api.service.impl;

import com.SteamGame.api.client.steam.SteamWebApiClient;
import com.SteamGame.api.domain.achievement.GameAchievementGlobal;
import com.SteamGame.api.dto.achievement.AchievementGlobalPercentDTO;
import com.SteamGame.api.dto.achievement.AchievementGlobalResultDTO;
import com.SteamGame.api.mapper.GameAchievementGlobalMapper;
import com.SteamGame.api.service.GameAchievementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class GameAchievementServiceImpl implements GameAchievementService {

    private static final Logger log = LoggerFactory.getLogger(GameAchievementServiceImpl.class);

    private final SteamWebApiClient webApiClient;
    private final GameAchievementGlobalMapper achievementMapper;

    public GameAchievementServiceImpl(SteamWebApiClient webApiClient, GameAchievementGlobalMapper achievementMapper) {
        this.webApiClient = webApiClient;
        this.achievementMapper = achievementMapper;
    }

    @Override
    public AchievementGlobalResultDTO getGlobalPercentages(Long appid) {
        try {
            List<AchievementGlobalPercentDTO> percentages = webApiClient.getGlobalAchievementPercentages(appid);
            if (percentages != null) {
                Timestamp now = new Timestamp(System.currentTimeMillis());
                for (var p : percentages) {
                    GameAchievementGlobal ach = new GameAchievementGlobal();
                    ach.setAppid(appid);
                    ach.setName(p.getName());
                    ach.setPercent(p.getPercent());
                    ach.setSyncedAt(now);
                    achievementMapper.upsert(ach);
                }
            }
            AchievementGlobalResultDTO result = new AchievementGlobalResultDTO();
            result.setAppid(appid);
            result.setAchievements(percentages);
            return result;
        } catch (Exception e) {
            log.warn("Failed to fetch achievements for appid={}: {}", appid, e.getMessage());
            AchievementGlobalResultDTO fallback = new AchievementGlobalResultDTO();
            fallback.setAppid(appid);
            fallback.setAchievements(List.of());
            return fallback;
        }
    }
}
