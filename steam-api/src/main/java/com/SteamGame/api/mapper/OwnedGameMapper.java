package com.SteamGame.api.mapper;

import com.SteamGame.api.domain.OwnedGame;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OwnedGameMapper {

    /**
     * 使用 H2 MERGE INTO 实现 upsert —— 存在则更新，不存在则插入。
     */
    @Insert("MERGE INTO owned_game (user_id, steam_id, appid, name, playtime_forever, last_synced_at) " +
            "KEY (user_id, appid) " +
            "VALUES (#{userId}, #{steamId}, #{appid}, #{name}, #{playtimeForever}, #{lastSyncedAt})")
    void upsert(OwnedGame game);

    /**
     * 按用户 ID 查询游戏列表。
     */
    @Select("SELECT id, user_id AS userId, steam_id AS steamId, appid, name, " +
            "playtime_forever AS playtimeForever, last_synced_at AS lastSyncedAt, " +
            "created_at AS createdAt, updated_at AS updatedAt " +
            "FROM owned_game WHERE user_id = #{userId} ORDER BY name")
    List<OwnedGame> listByUserId(@Param("userId") String userId);

    /**
     * 按用户 ID 统计游戏总数。
     */
    @Select("SELECT COUNT(*) FROM owned_game WHERE user_id = #{userId}")
    int countByUserId(@Param("userId") String userId);
}
