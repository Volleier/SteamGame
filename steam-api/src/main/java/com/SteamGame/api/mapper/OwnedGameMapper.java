package com.SteamGame.api.mapper;

import com.SteamGame.api.domain.OwnedGame;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OwnedGameMapper {

    @Insert("INSERT INTO owned_game(user_id, steam_id, appid, name, playtime_forever, last_synced_at) " +
            "VALUES(#{userId}, #{steamId}, #{appid}, #{name}, #{playtimeForever}, #{lastSyncedAt})")
    void insert(OwnedGame game);

    // 根据 appid 查询游戏
    @Select("SELECT id, user_id AS userId, steam_id AS steamId, appid, name, " +
            "playtime_forever AS playtimeForever, last_synced_at AS lastSyncedAt, " +
            "created_at AS createdAt, updated_at AS updatedAt " +
            "FROM owned_game WHERE appid = #{appid}")
    List<OwnedGame> findByAppid(@Param("appid") Long appid);

    // 根据 appid 更新游戏记录
    @org.apache.ibatis.annotations.Update("UPDATE owned_game SET name = #{name}, " +
            "playtime_forever = #{playtimeForever}, last_synced_at = #{lastSyncedAt}, " +
            "updated_at = CURRENT_TIMESTAMP WHERE appid = #{appid}")
    void updateByAppid(OwnedGame game);

    @Select("SELECT id, user_id AS userId, steam_id AS steamId, appid, name, " +
            "playtime_forever AS playtimeForever, last_synced_at AS lastSyncedAt, " +
            "created_at AS createdAt, updated_at AS updatedAt FROM owned_game")
    List<OwnedGame> listAll();
}
