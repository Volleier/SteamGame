package com.SteamGame.api.dto.player;

import java.util.List;

/**
 * DTO for recent games response (GET /api/player/recent-games).
 */
public class RecentGameResultDTO {
    private Integer totalCount;
    private List<RecentGameDTO> games;

    public Integer getTotalCount() { return totalCount; }
    public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }
    public List<RecentGameDTO> getGames() { return games; }
    public void setGames(List<RecentGameDTO> games) { this.games = games; }
}
