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
                
                // 无论是否拉取到有效开发商/发行商，都强制写入数据库，确保 details_synced_at 不为 null
                ownedGameMapper.updateDetails(
                    userId,
                    game.getAppid(),
                    game.getDeveloper() != null ? game.getDeveloper() : "Unknown",
                    game.getPublisher() != null ? game.getPublisher() : "Unknown",
                    game.getReleaseDate() != null ? game.getReleaseDate() : "Unknown",
                    game.getTags() != null ? game.getTags() : ""
                );
                
                logger.info("成功同步并更新游戏详情 (userId={}, appid={})", userId, game.getAppid());
                updated++;
                
                // 每次请求后休眠 1.5 秒，防频率限制
                Thread.sleep(1500);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                logger.warn("拉取游戏详情失败 (appid={}): {}", game.getAppid(), e.getMessage());
                // 发生网络或接口异常的游戏，也写入占位同步标记，防止进度条卡死
                try {
                    ownedGameMapper.updateDetails(
                        userId,
                        game.getAppid(),
                        "Unknown",
                        "Unknown",
                        "Unknown",
                        ""
                    );
                } catch (Exception ex) {
                    logger.error("写入异常占位同步标记失败 (appid={})", game.getAppid(), ex);
                }
            }
        }
        logger.info("细节补全完成 — userId={}, 已更新 {} 款游戏", userId, updated);
        return updated;
    }

    @Override
    public boolean isFetching() {
        return isFetchingDetails.get();
    }
}
