package com.SteamGame.api.controller;

import com.SteamGame.api.dto.stats.CurrentPlayerBatchDTO;
import com.SteamGame.api.dto.stats.CurrentPlayerBatchRequest;
import com.SteamGame.api.dto.stats.CurrentPlayerCountDTO;
import com.SteamGame.api.service.GameStatsService;
import com.SteamGame.common.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/game-stats")
public class GameStatsController {

    private final GameStatsService statsService;

    public GameStatsController(GameStatsService statsService) {
        this.statsService = statsService;
    }

    /**
     * GET /api/game-stats/current-players?appid=xxx
     */
    @GetMapping("/current-players")
    public ApiResponse<CurrentPlayerCountDTO> getCurrentPlayers(
            @RequestParam Long appid,
            @RequestParam(defaultValue = "false") boolean forceRefresh) {
        return ApiResponse.ok(statsService.getCurrentPlayers(appid, forceRefresh));
    }

    /**
     * POST /api/game-stats/current-players/batch
     */
    @PostMapping("/current-players/batch")
    public ApiResponse<CurrentPlayerBatchDTO> getCurrentPlayersBatch(@RequestBody CurrentPlayerBatchRequest request) {
        List<CurrentPlayerCountDTO> items = statsService.getCurrentPlayersBatch(
                request.getAppids(), request.getForceRefresh() != null && request.getForceRefresh());
        CurrentPlayerBatchDTO batch = new CurrentPlayerBatchDTO();
        batch.setItems(items);
        return ApiResponse.ok(batch);
    }
}
