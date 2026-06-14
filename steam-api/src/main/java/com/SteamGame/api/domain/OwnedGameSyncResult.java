package com.SteamGame.api.domain;

import java.util.List;

/**
 * 同步结果 —— 包含同步统计和最新游戏列表。
 */
public class OwnedGameSyncResult {
    private int total;
    private int saved;
    private List<OwnedGame> games;
    private boolean detailsInProgress;

    public OwnedGameSyncResult() {
    }

    public OwnedGameSyncResult(int total, int saved, List<OwnedGame> games, boolean detailsInProgress) {
        this.total = total;
        this.saved = saved;
        this.games = games;
        this.detailsInProgress = detailsInProgress;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getSaved() {
        return saved;
    }

    public void setSaved(int saved) {
        this.saved = saved;
    }

    public List<OwnedGame> getGames() {
        return games;
    }

    public void setGames(List<OwnedGame> games) {
        this.games = games;
    }

    public boolean isDetailsInProgress() {
        return detailsInProgress;
    }

    public void setDetailsInProgress(boolean detailsInProgress) {
        this.detailsInProgress = detailsInProgress;
    }
}
