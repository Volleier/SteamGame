package com.SteamGame.api.controller;

import com.SteamGame.api.domain.OwnedGame;
import com.SteamGame.api.domain.OwnedGameSyncResult;
import com.SteamGame.api.dto.OwnedGameCountDTO;
import com.SteamGame.api.dto.OwnedGameDTO;
import com.SteamGame.api.dto.OwnedGameDtoConverter;
import com.SteamGame.common.context.CurrentUser;
import com.SteamGame.common.context.CurrentUserProvider;
import com.SteamGame.common.error.BusinessException;
import com.SteamGame.api.service.OwnedGameService;
import com.SteamGame.common.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ownedgames")
public class OwnedGamesController {

    private static final Logger logger = LoggerFactory.getLogger(OwnedGamesController.class);

    private final OwnedGameService ownedGameService;
    private final CurrentUserProvider currentUserProvider;
    private final OwnedGameDtoConverter converter = new OwnedGameDtoConverter();

    public OwnedGamesController(OwnedGameService ownedGameService, CurrentUserProvider currentUserProvider) {
        this.ownedGameService = ownedGameService;
        this.currentUserProvider = currentUserProvider;
    }

    /**
     * 同步玩家游戏库：从 Steam 拉取 → 写入本地数据库 → 返回最新列表。
     */
    @PostMapping("/sync")
    public ApiResponse<List<OwnedGameDTO>> sync() {
        try {
            CurrentUser currentUser = currentUserProvider.currentUser();
            OwnedGameSyncResult result = ownedGameService.syncOwnedGamesWithResult(currentUser.getUserId());
            logger.info("POST /sync — 同步完成，userId={}，共 {} 款游戏", currentUser.getUserId(), result.getTotal());
            return ApiResponse.ok(converter.toDtoList(result.getGames()));
        } catch (BusinessException e) {
            logger.warn("同步游戏库业务失败: {}", e.getMessage());
            return ApiResponse.fail(e.getErrorCode().getCode(), e.getMessage());
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
        String userId = currentUserProvider.currentUser().getUserId();
        List<OwnedGame> games = ownedGameService.listOwnedGames(userId);
        return ApiResponse.ok(converter.toDtoList(games));
    }

    /**
     * 查询本地游戏总数（只读数据库）。
     */
    @GetMapping("/count")
    public ApiResponse<OwnedGameCountDTO> count() {
        String userId = currentUserProvider.currentUser().getUserId();
        int count = ownedGameService.countOwnedGames(userId);
        return ApiResponse.ok(new OwnedGameCountDTO(count));
    }

}
