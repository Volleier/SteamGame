package com.SteamGame.api.service.impl;

import com.SteamGame.api.client.SteamApiClient;
import com.SteamGame.api.domain.OwnedGame;
import com.SteamGame.api.domain.OwnedGameSyncResult;
import com.SteamGame.api.mapper.OwnedGameMapper;
import com.SteamGame.api.service.OwnedGameDetailsService;
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

/**
 * OwnedGameService 默认实现 —— 编排凭证获取、Steam 同步、数据库持久化。
 * 详情补全委托给 OwnedGameDetailsService。
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

    @Autowired
    private OwnedGameDetailsService detailsService;

    @Override
    @Transactional
    public List<OwnedGame> syncOwnedGames(String userId) {
        return doSync(userId).getGames();
    }

    /**
     * 同步并返回包含统计信息的完整结果。
     */
    public OwnedGameSyncResult syncOwnedGamesWithResult(String userId) {
        return doSync(userId);
    }

    private OwnedGameSyncResult doSync(String userId) {
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
            List<OwnedGame> local = listOwnedGames(uid);
            return new OwnedGameSyncResult(local.size(), 0, local, false);
        }

        if (games.isEmpty()) {
            logger.info("Steam 返回空游戏列表 (userId={})，可能资料私密或无游戏", uid);
            List<OwnedGame> local = listOwnedGames(uid);
            return new OwnedGameSyncResult(local.size(), 0, local, false);
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
        detailsService.enqueueMissingDetails(uid);

        // 4) 返回数据库最新列表
        List<OwnedGame> latest = listOwnedGames(uid);
        return new OwnedGameSyncResult(games.size(), savedCount, latest, true);
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

    @PostConstruct
    public void init() {
        // 项目启动时自动触发后台拉取缺失的开发商/发行商数据（针对默认用户）
        detailsService.enqueueMissingDetails("default");
    }
}
