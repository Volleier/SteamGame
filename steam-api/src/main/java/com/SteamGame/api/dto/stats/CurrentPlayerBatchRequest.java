package com.SteamGame.api.dto.stats;

import java.util.List;

/**
 * Request DTO for batch player count query (POST /api/game-stats/current-players/batch).
 */
public class CurrentPlayerBatchRequest {
    private List<Long> appids;
    private Boolean forceRefresh;

    public List<Long> getAppids() { return appids; }
    public void setAppids(List<Long> appids) { this.appids = appids; }
    public Boolean getForceRefresh() { return forceRefresh; }
    public void setForceRefresh(Boolean forceRefresh) { this.forceRefresh = forceRefresh; }
}
