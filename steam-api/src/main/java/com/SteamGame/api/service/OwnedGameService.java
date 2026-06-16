package com.SteamGame.api.service;

import com.SteamGame.api.domain.OwnedGame;
import com.SteamGame.api.domain.OwnedGameSyncResult;

import java.util.List;

/**
 * 玩家游戏库服务 —— 承接完整的同步、查询、统计流程。
 */
public interface OwnedGameService {

    /**
     * 从 Steam 同步当前用户的游戏库到本地数据库，并返回最新列表。
     *
     * @param userId 本地用户 ID
     * @return 同步后的游戏列表
     */
    List<OwnedGame> syncOwnedGames(String userId);

    /**
     * 从 Steam 同步当前用户的游戏库到本地数据库，并返回同步统计和最新列表。
     *
     * @param userId 本地用户 ID
     * @return 同步结果
     */
    OwnedGameSyncResult syncOwnedGamesWithResult(String userId);

    /**
     * 从本地数据库查询当前用户的游戏列表。
     *
     * @param userId 本地用户 ID
     * @return 本地游戏列表
     */
    List<OwnedGame> listOwnedGames(String userId);

    /**
     * 统计本地数据库中当前用户的游戏总数。
     *
     * @param userId 本地用户 ID
     * @return 游戏数量
     */
    int countOwnedGames(String userId);

    /**
     * 统计当前用户缺失详情的游戏数量。
     *
     * @param userId 本地用户 ID
     * @return 缺失数量
     */
    int countMissingDetails(String userId);
}
