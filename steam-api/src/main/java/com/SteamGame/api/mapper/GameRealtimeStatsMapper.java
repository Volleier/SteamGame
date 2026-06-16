package com.SteamGame.api.mapper;

import com.SteamGame.api.domain.stats.GameRealtimeStats;
import org.apache.ibatis.annotations.*;

@Mapper
public interface GameRealtimeStatsMapper {

    @Insert("MERGE INTO game_realtime_stats (appid, player_count, cached, stale, synced_at, updated_at) " +
            "KEY (appid) " +
            "VALUES (#{appid}, #{playerCount}, #{cached}, #{stale}, #{syncedAt}, #{updatedAt})")
    void upsert(GameRealtimeStats stats);

    @Select("SELECT appid, player_count AS playerCount, cached, stale, " +
            "synced_at AS syncedAt, updated_at AS updatedAt " +
            "FROM game_realtime_stats WHERE appid = #{appid}")
    GameRealtimeStats findByAppid(@Param("appid") Long appid);
}
