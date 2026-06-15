package com.SteamGame.api.mapper;

import com.SteamGame.api.domain.player.RecentGame;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RecentGameMapper {

    @Insert("MERGE INTO recent_game (user_id, appid, name, playtime_2weeks, playtime_forever, icon_url, synced_at) " +
            "KEY (user_id, appid) " +
            "VALUES (#{userId}, #{appid}, #{name}, #{playtime2weeks}, #{playtimeForever}, #{iconUrl}, #{syncedAt})")
    void upsert(RecentGame game);

    @Select("SELECT id, user_id AS userId, appid, name, playtime_2weeks AS playtime2weeks, " +
            "playtime_forever AS playtimeForever, icon_url AS iconUrl, synced_at AS syncedAt " +
            "FROM recent_game WHERE user_id = #{userId} ORDER BY playtime_2weeks DESC LIMIT #{limit}")
    List<RecentGame> findByUserId(@Param("userId") String userId, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM recent_game WHERE user_id = #{userId}")
    int countByUserId(@Param("userId") String userId);
}
