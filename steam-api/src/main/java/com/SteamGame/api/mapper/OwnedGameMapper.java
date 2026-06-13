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
     * 不要在 MERGE INTO 中包含 developer 和 publisher 字段，以防同步时已存在的记录被重置为 NULL。
     */
    @Insert("MERGE INTO owned_game (user_id, steam_id, appid, name, playtime_forever, last_synced_at) " +
            "KEY (user_id, appid) " +
            "VALUES (#{userId}, #{steamId}, #{appid}, #{name}, #{playtimeForever}, #{lastSyncedAt})")
    void upsert(OwnedGame game);

    /**
     * 按用户 ID 查询游戏列表。
     */
    @Select("SELECT id, user_id AS userId, steam_id AS steamId, appid, name, " +
            "playtime_forever AS playtimeForever, developer, publisher, " +
            "release_date AS releaseDate, tags, last_synced_at AS lastSyncedAt, " +
            "created_at AS createdAt, updated_at AS updatedAt " +
            "FROM owned_game WHERE user_id = #{userId} ORDER BY name")
    List<OwnedGame> listByUserId(@Param("userId") String userId);

    /**
     * 更新游戏的开发商、发行商、发行日期和标签。
     */
    @org.apache.ibatis.annotations.Update(
            "UPDATE owned_game SET developer = #{developer}, publisher = #{publisher}, " +
            "release_date = #{releaseDate}, tags = #{tags} " +
            "WHERE appid = #{appid}")
    void updateDetails(@Param("appid") Long appid,
                       @Param("developer") String developer,
                       @Param("publisher") String publisher,
                       @Param("releaseDate") String releaseDate,
                       @Param("tags") String tags);

    /**
     * 查询所有开发商或发行商为空的游戏。
     */
    @Select("SELECT id, user_id AS userId, steam_id AS steamId, appid, name, " +
            "playtime_forever AS playtimeForever, developer, publisher, " +
            "release_date AS releaseDate, tags, last_synced_at AS lastSyncedAt " +
            "FROM owned_game WHERE developer IS NULL OR publisher IS NULL")
    List<OwnedGame> listMissingDetails();

    /**
     * 按用户 ID 统计游戏总数。
     */
    @Select("SELECT COUNT(*) FROM owned_game WHERE user_id = #{userId}")
    int countByUserId(@Param("userId") String userId);
}
