package com.SteamGame.api.service;

import com.SteamGame.api.dto.achievement.AchievementGlobalResultDTO;

public interface GameAchievementService {
    AchievementGlobalResultDTO getGlobalPercentages(Long appid);
}
