package com.SteamGame.api.controller;

import com.SteamGame.api.domain.OwnedGame;
import com.SteamGame.api.dto.OwnedGameCountDTO;
import com.SteamGame.api.dto.OwnedGameDTO;
import com.SteamGame.api.dto.OwnedGameDtoConverter;
import com.SteamGame.api.service.OwnedGameService;
import com.SteamGame.api.service.SteamApiService;
import com.SteamGame.common.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ownedgames")
public class OwnedGamesController {

    private static final Logger logger = LoggerFactory.getLogger(OwnedGamesController.class);

    private final OwnedGameService ownedGameService;
    private final SteamApiService steamApiService;
    private final OwnedGameDtoConverter converter = new OwnedGameDtoConverter();

    public OwnedGamesController(OwnedGameService ownedGameService, SteamApiService steamApiService) {
        this.ownedGameService = ownedGameService;
        this.steamApiService = steamApiService;
    }

    /**
     * 同步玩家游戏库：从 Steam 拉取 → 写入本地数据库 → 返回最新列表。
     */
    @PostMapping("/sync")
    public ApiResponse<List<OwnedGameDTO>> sync() {
        try {
            List<OwnedGame> games = ownedGameService.syncOwnedGames(null);
            logger.info("POST /sync — 同步完成，共 {} 款游戏", games.size());
            return ApiResponse.ok(converter.toDtoList(games));
        } catch (Exception e) {
            logger.error("同步游戏库失败: {}", e.getMessage(), e);
            return ApiResponse.fail(500, "同步失败: " + e.getMessage());
        }
    }

    /**
     * 查询本地游戏列表（只读数据库）。
     */
    @GetMapping("/list")
    public ApiResponse<List<OwnedGameDTO>> list() {
        List<OwnedGame> games = ownedGameService.listOwnedGames(null);
        return ApiResponse.ok(converter.toDtoList(games));
    }

    /**
     * 查询本地游戏总数（只读数据库）。
     */
    @GetMapping("/count")
    public ApiResponse<OwnedGameCountDTO> count() {
        int count = ownedGameService.countOwnedGames(null);
        return ApiResponse.ok(new OwnedGameCountDTO(count));
    }

    /**
     * [调试] 通过 steamId + apiKey 直接从 Steam 拉取并写入数据库。
     * 正式链路请使用 POST /sync。
     */
    @GetMapping("/fetch")
    public List<OwnedGame> fetchAndList(@RequestParam String steamId, @RequestParam String apiKey) {
        try {
            String body = steamApiService.getOwnedGames(steamId, apiKey);
            logger.info("Fetched OwnedGames result: {}", body);
        } catch (Exception e) {
            logger.warn("Error fetching owned games: {}", e.getMessage());
        }
        return ownedGameService.listOwnedGames(null);
    }
}
