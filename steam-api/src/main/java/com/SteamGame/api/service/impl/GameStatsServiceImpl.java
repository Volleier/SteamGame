package com.SteamGame.api.service.impl;

import com.SteamGame.api.client.steam.SteamWebApiClient;
import com.SteamGame.api.domain.stats.GameRealtimeStats;
import com.SteamGame.api.dto.stats.CurrentPlayerCountDTO;
import com.SteamGame.api.mapper.GameRealtimeStatsMapper;
import com.SteamGame.api.service.GameStatsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class GameStatsServiceImpl implements GameStatsService {

    private static final Logger log = LoggerFactory.getLogger(GameStatsServiceImpl.class);
    private static final long CACHE_TTL_MS = 10 * 60 * 1000; // 10 minutes

    private final SteamWebApiClient webApiClient;
    private final GameRealtimeStatsMapper statsMapper;

    public GameStatsServiceImpl(SteamWebApiClient webApiClient, GameRealtimeStatsMapper statsMapper) {
        this.webApiClient = webApiClient;
        this.statsMapper = statsMapper;
    }

    @Override
    public CurrentPlayerCountDTO getCurrentPlayers(Long appid, boolean forceRefresh) {
        // Check cache
        if (!forceRefresh) {
            GameRealtimeStats cached = statsMapper.findByAppid(appid);
            if (cached != null && cached.getSyncedAt() != null) {
                long age = System.currentTimeMillis() - cached.getSyncedAt().getTime();
                if (age < CACHE_TTL_MS) {
                    CurrentPlayerCountDTO dto = new CurrentPlayerCountDTO();
                    dto.setAppid(appid);
                    dto.setPlayerCount(cached.getPlayerCount());
                    dto.setCached(true);
                    dto.setStale(cached.getStale());
                    dto.setSyncedAt(cached.getSyncedAt());
                    return dto;
                }
            }
        }

        // Fetch from Steam
        try {
            CurrentPlayerCountDTO fresh = webApiClient.getCurrentPlayers(appid);
            if (fresh != null && fresh.getPlayerCount() != null) {
                GameRealtimeStats stats = new GameRealtimeStats();
                stats.setAppid(appid);
                stats.setPlayerCount(fresh.getPlayerCount());
                stats.setCached(false);
                stats.setStale(false);
                Timestamp now = new Timestamp(System.currentTimeMillis());
                stats.setSyncedAt(now);
                stats.setUpdatedAt(now);
                statsMapper.upsert(stats);
                fresh.setCached(false);
                fresh.setSyncedAt(now);
                return fresh;
            }
        } catch (Exception e) {
            log.warn("Failed to fetch current players for appid={}: {}", appid, e.getMessage());
        }

        // Return stale cache if available
        GameRealtimeStats stale = statsMapper.findByAppid(appid);
        if (stale != null) {
            CurrentPlayerCountDTO dto = new CurrentPlayerCountDTO();
            dto.setAppid(appid);
            dto.setPlayerCount(stale.getPlayerCount());
            dto.setCached(true);
            dto.setStale(true);
            dto.setSyncedAt(stale.getSyncedAt());
            return dto;
        }

        CurrentPlayerCountDTO empty = new CurrentPlayerCountDTO();
        empty.setAppid(appid);
        empty.setPlayerCount(0);
        empty.setCached(false);
        empty.setStale(true);
        return empty;
    }

    @Override
    public List<CurrentPlayerCountDTO> getCurrentPlayersBatch(List<Long> appids, boolean forceRefresh) {
        int limit = Math.min(appids.size(), 50);
        List<CurrentPlayerCountDTO> results = new ArrayList<>();
        for (int i = 0; i < limit; i++) {
            results.add(getCurrentPlayers(appids.get(i), forceRefresh));
        }
        return results;
    }
}
