package com.SteamGame.api.controller;

import com.SteamGame.api.dto.achievement.AchievementGlobalResultDTO;
import com.SteamGame.api.service.GameAchievementService;
import com.SteamGame.common.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class GameAchievementController {

    private final GameAchievementService achievementService;

    public GameAchievementController(GameAchievementService achievementService) { this.achievementService = achievementService; }

    @GetMapping("/game-achievements/global-percentages")
    public ApiResponse<AchievementGlobalResultDTO> getGlobalPercentages(@RequestParam Long appid) {
        return ApiResponse.ok(achievementService.getGlobalPercentages(appid));
    }
}
