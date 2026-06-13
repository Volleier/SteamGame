package com.SteamGame.login.service.impl;

import com.SteamGame.api.client.SteamApiClient;
import com.SteamGame.api.domain.OwnedGame;
import com.SteamGame.api.domain.SteamCredential;
import com.SteamGame.api.mapper.OwnedGameMapper;
import com.SteamGame.api.service.CredentialProvider;
import com.SteamGame.api.service.OwnedGameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

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
            logger.warn("未找到有效凭证 (userId={})，无法同步游戏库", uid);
            return Collections.emptyList();
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

        // 3) 将游戏列表 upsert 到本地数据库
        int savedCount = 0;
        for (OwnedGame game : games) {
            try {
                List<OwnedGame> existing = ownedGameMapper.findByAppid(game.getAppid());
                if (existing != null && !existing.isEmpty()) {
                    ownedGameMapper.updateByAppid(game);
                } else {
                    ownedGameMapper.insert(game);
                }
                savedCount++;
            } catch (Exception e) {
                logger.warn("保存游戏记录失败 (appid={}): {}", game.getAppid(), e.getMessage());
            }
        }
        logger.info("同步完成 — userId={}, 获取 {} 款游戏, 成功保存 {} 款", uid, games.size(), savedCount);

        // 4) 返回数据库最新列表
        return listOwnedGames(uid);
    }

    @Override
    public List<OwnedGame> listOwnedGames(String userId) {
        return ownedGameMapper.listAll();
    }

    @Override
    public int countOwnedGames(String userId) {
        return ownedGameMapper.listAll().size();
    }

    private String userIdOrDefault(String userId) {
        return userId != null && !userId.isEmpty() ? userId : "default";
    }
}
