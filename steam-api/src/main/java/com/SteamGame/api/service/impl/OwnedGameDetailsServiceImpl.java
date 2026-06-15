package com.SteamGame.api.service.impl;

import com.SteamGame.api.client.SteamApiClient;
import com.SteamGame.api.domain.OwnedGame;
import com.SteamGame.api.mapper.OwnedGameMapper;
import com.SteamGame.api.service.OwnedGameDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * OwnedGameDetailsService 默认实现 —— 使用 Spring 管理线程池异步补全游戏详情。
 */
@Service
public class OwnedGameDetailsServiceImpl implements OwnedGameDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(OwnedGameDetailsServiceImpl.class);

    @Autowired
    private SteamApiClient steamApiClient;

    @Autowired
    private OwnedGameMapper ownedGameMapper;

    @Autowired
    @Qualifier("gameDetailsExecutor")
    private Executor gameDetailsExecutor;

    private final AtomicBoolean isFetchingDetails = new AtomicBoolean(false);

    @Override
    public void enqueueMissingDetails(String userId) {
        if (isFetchingDetails.compareAndSet(false, true)) {
            CompletableFuture.runAsync(() -> {
                try {
                    syncMissingDetails(userId, 100);
                } finally {
                    isFetchingDetails.set(false);
                    logger.info("后台游戏详细信息拉取任务结束（userId={}）。", userId);
                }
            }, gameDetailsExecutor);
        } else {
            logger.info("后台补全任务已在运行中，跳过本次调度（userId={}）。", userId);
        }
    }

    @Override
    public int syncMissingDetails(String userId, int limit) {
        logger.info("开始后台拉取游戏详细信息（userId={}）...", userId);
        List<OwnedGame> missing = ownedGameMapper.listMissingDetailsByUserId(userId, limit);
        logger.info("userId={} 发现 {} 款游戏缺失详细信息（limit={}）", userId, missing.size(), limit);

        int updated = 0;
        for (OwnedGame game : missing) {
            try {
                steamApiClient.fillGameDetails(game);
                if (game.getDeveloper() != null || game.getPublisher() != null) {
                    java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
                    game.setDetailsSyncedAt(now);
                    ownedGameMapper.updateDetails(
                        userId,
                        game.getAppid(),
                        game.getDeveloper(),
                        game.getPublisher(),
                        game.getReleaseDate(),
                        game.getTags()
                    );
                    logger.info("成功更新游戏 (userId={}, appid={}) 的开发商为 [{}]，发行商为 [{}]",
                            userId, game.getAppid(), game.getDeveloper(), game.getPublisher());
                    updated++;
                }
                // 每次请求后休眠 1.5 秒，防频率限制
                Thread.sleep(1500);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                logger.warn("拉取游戏详情失败 (appid={}): {}", game.getAppid(), e.getMessage());
            }
        }
        logger.info("细节补全完成 — userId={}, 已更新 {} 款游戏", userId, updated);
        return updated;
    }
}
