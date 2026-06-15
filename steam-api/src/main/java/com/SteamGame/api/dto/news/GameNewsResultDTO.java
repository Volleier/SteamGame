package com.SteamGame.api.dto.news;

import java.util.List;

/**
 * DTO for game news response (GET /api/game-news).
 */
public class GameNewsResultDTO {
    private Long appid;
    private List<GameNewsDTO> items;

    public Long getAppid() { return appid; }
    public void setAppid(Long appid) { this.appid = appid; }
    public List<GameNewsDTO> getItems() { return items; }
    public void setItems(List<GameNewsDTO> items) { this.items = items; }
}
