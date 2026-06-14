package com.SteamGame.api.service.impl;

import com.SteamGame.api.client.SteamApiClient;
import com.SteamGame.api.domain.OwnedGame;
import com.SteamGame.api.mapper.OwnedGameMapper;
import com.SteamGame.api.service.OwnedGameService;
import com.SteamGame.common.context.CredentialProvider;
import com.SteamGame.common.context.SteamCredential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import jakarta.annotation.PostConstruct;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * OwnedGameService 默认实现 —— 编排凭证获取、Steam 同步、数据库持久化。
 */
@Service
public class OwnedGameServiceImpl implements OwnedGameService {

    private static final Logger logger = LoggerFactory.getLogger(OwnedGameServiceImpl.class);

    @Autowired
    private CredentialProvider credentialProvider;

    @Autowired
    private SteamApiClient steamApiClient;

    @Autowired
    private OwnedGameMapper ownedGameMapper;

    @Override
    @Transactional
    public List<OwnedGame> syncOwnedGames(String userId) {
        String uid = userIdOrDefault(userId);

        // 1) 读取当前用户凭证
        SteamCredential credential = credentialProvider.getCurrentCredential(uid);
        if (!credential.isValid()) {
            String msg = "未找到有效 Steam 凭据 (userId=" + uid + ")，请先在设置页配置 SteamID 和 API Key";
            logger.warn(msg);
            throw new RuntimeException(msg);
        }

        // 2) 调用 Steam API 获取游戏列表
        List<OwnedGame> games;
        try {
            games = steamApiClient.fetchOwnedGames(credential.getSteamId(), credential.getApiKey());
        } catch (Exception e) {
            logger.error("Steam API 请求失败: {}", e.getMessage(), e);
            // Steam API 不可用时返回本地已有数据，不清空数据库
            return listOwnedGames(uid);
        }

        if (games.isEmpty()) {
            logger.info("Steam 返回空游戏列表 (userId={})，可能资料私密或无游戏", uid);
            return listOwnedGames(uid);
        }

        // 3) 设置用户归属字段，然后使用 MERGE INTO upsert 写入本地数据库
        java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
        int savedCount = 0;
        for (OwnedGame game : games) {
            game.setUserId(uid);
            game.setSteamId(credential.getSteamId());
            game.setLastSyncedAt(now);
            try {
                ownedGameMapper.upsert(game);
                savedCount++;
            } catch (Exception e) {
                logger.warn("upsert 游戏记录失败 (appid={}): {}", game.getAppid(), e.getMessage());
            }
        }
        logger.info("同步完成 — userId={}, 获取 {} 款游戏, 成功 upsert {} 款", uid, games.size(), savedCount);

        // 异步触发后台拉取开发商和发行商真实数据
        triggerBackgroundDetailsFetch(uid);

        // 4) 返回数据库最新列表
        return listOwnedGames(uid);
    }

    @Override
    public List<OwnedGame> listOwnedGames(String userId) {
        return ownedGameMapper.listByUserId(userIdOrDefault(userId));
    }

    @Override
    public int countOwnedGames(String userId) {
        return ownedGameMapper.countByUserId(userIdOrDefault(userId));
    }

    private String userIdOrDefault(String userId) {
        return userId != null && !userId.isEmpty() ? userId : "default";
    }

    private final AtomicBoolean isFetchingDetails = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        // 项目启动时自动触发后台拉取缺失的开发商/发行商数据（针对默认用户）
        triggerBackgroundDetailsFetch("default");
    }

    /**
     * 异步触发后台补全游戏详情（开发商、发行商、发行日期、标签）。
     * 使用 AtomicBoolean 防止重复调度，每次最多处理 100 条，请求间隔 1.5 秒。
     *
     * @param userId 用户 ID
     */
    private void triggerBackgroundDetailsFetch(String userId) {
        if (isFetchingDetails.compareAndSet(false, true)) {
            CompletableFuture.runAsync(() -> {
                try {
                    logger.info("开始后台拉取游戏详细信息（userId={}）...", userId);
                    int limit = 100;
                    List<OwnedGame> missing = ownedGameMapper.listMissingDetailsByUserId(userId, limit);
                    logger.info("userId={} 发现 {} 款游戏缺失详细信息（limit={}）", userId, missing.size(), limit);
                    for (OwnedGame game : missing) {
                        try {
                            steamApiClient.fillGameDetails(game);
                            if (game.getDeveloper() != null || game.getPublisher() != null) {
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
                } finally {
                    isFetchingDetails.set(false);
                    logger.info("后台游戏详细信息拉取任务结束（userId={}）。", userId);
                }
            });
        }
    }
}
