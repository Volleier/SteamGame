package com.SteamGame.api.service.impl;

import com.SteamGame.api.client.steam.SteamWebApiClient;
import com.SteamGame.api.domain.player.RecentGame;
import com.SteamGame.api.dto.player.RecentGameDTO;
import com.SteamGame.api.dto.player.RecentGameResultDTO;
import com.SteamGame.api.mapper.RecentGameMapper;
import com.SteamGame.api.service.RecentGameService;
import com.SteamGame.common.context.CredentialProvider;
import com.SteamGame.common.context.SteamCredential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecentGameServiceImpl implements RecentGameService {

    private static final Logger log = LoggerFactory.getLogger(RecentGameServiceImpl.class);

    private final CredentialProvider credentialProvider;
    private final SteamWebApiClient webApiClient;
    private final RecentGameMapper recentGameMapper;

    public RecentGameServiceImpl(CredentialProvider credentialProvider,
                                  SteamWebApiClient webApiClient,
                                  RecentGameMapper recentGameMapper) {
        this.credentialProvider = credentialProvider;
        this.webApiClient = webApiClient;
        this.recentGameMapper = recentGameMapper;
    }

    @Override
    public RecentGameResultDTO getRecentGames(String userId, int count) {
        // Try Steam fetch first
        try {
            SteamCredential cred = credentialProvider.getCurrentCredential(userId);
            if (cred != null && cred.isValid()) {
                List<RecentGameDTO> dtos = webApiClient.getRecentlyPlayedGames(cred.getSteamId(), cred.getApiKey(), count);
                if (dtos != null && !dtos.isEmpty()) {
                    saveRecentGames(userId, dtos);
                    RecentGameResultDTO result = new RecentGameResultDTO();
                    result.setTotalCount(dtos.size());
                    result.setGames(dtos);
                    return result;
                }
            }
        } catch (Exception e) {
            log.warn("Failed to fetch recent games from Steam for userId={}: {}", userId, e.getMessage());
        }

        // Fallback to local cache
        List<RecentGame> cached = recentGameMapper.findByUserId(userId, count);
        List<RecentGameDTO> games = cached.stream().map(r -> {
            RecentGameDTO dto = new RecentGameDTO();
            dto.setAppid(r.getAppid());
            dto.setName(r.getName());
            dto.setPlaytime2Weeks(r.getPlaytime2weeks());
            dto.setPlaytimeForever(r.getPlaytimeForever());
            dto.setIconUrl(r.getIconUrl());
            return dto;
        }).collect(Collectors.toList());

        RecentGameResultDTO result = new RecentGameResultDTO();
        result.setTotalCount(games.size());
        result.setGames(games);
        return result;
    }

    private void saveRecentGames(String userId, List<RecentGameDTO> dtos) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        for (RecentGameDTO dto : dtos) {
            RecentGame game = new RecentGame();
            game.setUserId(userId);
            game.setAppid(dto.getAppid());
            game.setName(dto.getName());
            game.setPlaytime2weeks(dto.getPlaytime2Weeks());
            game.setPlaytimeForever(dto.getPlaytimeForever());
            game.setIconUrl(dto.getIconUrl());
            game.setSyncedAt(now);
            recentGameMapper.upsert(game);
        }
    }
}
