package com.SteamGame.api.mapper;

import com.SteamGame.api.domain.achievement.GameAchievementGlobal;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface GameAchievementGlobalMapper {

    @Insert("MERGE INTO game_achievement_global (appid, name, percent, synced_at) " +
            "KEY (appid, name) " +
            "VALUES (#{appid}, #{name}, #{percent}, #{syncedAt})")
    void upsert(GameAchievementGlobal ach);

    @Select("SELECT id, appid, name, percent, synced_at AS syncedAt " +
            "FROM game_achievement_global WHERE appid = #{appid}")
    List<GameAchievementGlobal> findByAppid(@Param("appid") Long appid);
}
