package com.SteamGame.api.dto.achievement;

/**
 * DTO for a single achievement global percentage.
 */
public class AchievementGlobalPercentDTO {
    private String name;
    private Double percent;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getPercent() { return percent; }
    public void setPercent(Double percent) { this.percent = percent; }
}
