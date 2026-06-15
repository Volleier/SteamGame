package com.SteamGame.api.service;

/**
 * 游戏详情补全服务 —— 负责异步拉取 Steam Store 详情并更新数据库。
 */
public interface OwnedGameDetailsService {

    /**
     * 将指定用户的缺失详情补全任务加入后台队列。
     *
     * @param userId 用户 ID
     */
    void enqueueMissingDetails(String userId);

    /**
     * 同步补全指定用户的缺失详情（最多 limit 条）。
     *
     * @param userId 用户 ID
     * @param limit  最大处理数量
     * @return 实际处理数量
     */
    int syncMissingDetails(String userId, int limit);
}
