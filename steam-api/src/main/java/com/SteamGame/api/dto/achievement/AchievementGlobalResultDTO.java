package com.SteamGame.api.dto.achievement;

import java.util.List;

/**
 * DTO for achievement global percentages response (GET /api/game-achievements/global-percentages).
 */
public class AchievementGlobalResultDTO {
    private Long appid;
    private List<AchievementGlobalPercentDTO> achievements;

    public Long getAppid() { return appid; }
    public void setAppid(Long appid) { this.appid = appid; }
    public List<AchievementGlobalPercentDTO> getAchievements() { return achievements; }
    public void setAchievements(List<AchievementGlobalPercentDTO> achievements) { this.achievements = achievements; }
}
