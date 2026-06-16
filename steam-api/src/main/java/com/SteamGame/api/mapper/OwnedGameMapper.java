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
    @Insert("MERGE INTO owned_game (user_id, steam_id, appid, name, playtime_forever, " +
            "playtime_2weeks, rtime_last_played, img_icon_url, has_community_visible_stats, " +
            "playtime_windows_forever, playtime_mac_forever, playtime_linux_forever, playtime_deck_forever, " +
            "last_synced_at, updated_at) " +
            "KEY (user_id, appid) " +
            "VALUES (#{userId}, #{steamId}, #{appid}, #{name}, #{playtimeForever}, " +
            "#{playtime2weeks}, #{rtimeLastPlayed}, #{imgIconUrl}, #{hasCommunityVisibleStats}, " +
            "#{playtimeWindowsForever}, #{playtimeMacForever}, #{playtimeLinuxForever}, #{playtimeDeckForever}, " +
            "#{lastSyncedAt}, #{updatedAt})")
    void upsert(OwnedGame game);

    /**
     * 按用户 ID 查询游戏列表。
     */
    @Select("SELECT id, user_id AS userId, steam_id AS steamId, appid, name, " +
            "playtime_forever AS playtimeForever, " +
            "playtime_2weeks AS playtime2weeks, rtime_last_played AS rtimeLastPlayed, " +
            "img_icon_url AS imgIconUrl, has_community_visible_stats AS hasCommunityVisibleStats, " +
            "playtime_windows_forever AS playtimeWindowsForever, playtime_mac_forever AS playtimeMacForever, " +
            "playtime_linux_forever AS playtimeLinuxForever, playtime_deck_forever AS playtimeDeckForever, " +
            "developer, publisher, " +
            "release_date AS releaseDate, tags, last_synced_at AS lastSyncedAt, " +
            "details_synced_at AS detailsSyncedAt, " +
            "created_at AS createdAt, updated_at AS updatedAt " +
            "FROM owned_game WHERE user_id = #{userId} ORDER BY name")
    List<OwnedGame> listByUserId(@Param("userId") String userId);

    /**
     * 更新游戏的开发商、发行商、发行日期和标签 —— 必须带 userId 防止多用户串数据。
     */
    @org.apache.ibatis.annotations.Update(
            "UPDATE owned_game SET developer = #{developer}, publisher = #{publisher}, " +
            "release_date = #{releaseDate}, tags = #{tags}, " +
            "details_synced_at = CURRENT_TIMESTAMP, updated_at = CURRENT_TIMESTAMP " +
            "WHERE user_id = #{userId} AND appid = #{appid}")
    void updateDetails(@Param("userId") String userId,
                       @Param("appid") Long appid,
                       @Param("developer") String developer,
                       @Param("publisher") String publisher,
                       @Param("releaseDate") String releaseDate,
                       @Param("tags") String tags);

    /**
     * 查询指定用户缺失详情的游戏，限制返回数量防止扫全表。
     */
    @Select("SELECT id, user_id AS userId, steam_id AS steamId, appid, name, " +
            "playtime_forever AS playtimeForever, " +
            "playtime_2weeks AS playtime2weeks, rtime_last_played AS rtimeLastPlayed, " +
            "img_icon_url AS imgIconUrl, has_community_visible_stats AS hasCommunityVisibleStats, " +
            "playtime_windows_forever AS playtimeWindowsForever, playtime_mac_forever AS playtimeMacForever, " +
            "playtime_linux_forever AS playtimeLinuxForever, playtime_deck_forever AS playtimeDeckForever, " +
            "developer, publisher, " +
            "release_date AS releaseDate, tags, last_synced_at AS lastSyncedAt, " +
            "details_synced_at AS detailsSyncedAt " +
            "FROM owned_game WHERE user_id = #{userId} AND (details_synced_at IS NULL OR developer IS NULL OR publisher IS NULL) " +
            "LIMIT #{limit}")
    List<OwnedGame> listMissingDetailsByUserId(@Param("userId") String userId, @Param("limit") int limit);

    /**
     * 按用户 ID 统计游戏总数。
     */
    @Select("SELECT COUNT(*) FROM owned_game WHERE user_id = #{userId}")
    int countByUserId(@Param("userId") String userId);

    @Select("SELECT COUNT(*) FROM owned_game WHERE user_id = #{userId} AND (details_synced_at IS NULL OR developer IS NULL OR publisher IS NULL)")
    int countMissingDetailsByUserId(@Param("userId") String userId);

    @Select("SELECT COALESCE(SUM(playtime_forever), 0) FROM owned_game WHERE user_id = #{userId}")
    Long sumPlaytimeForeverByUserId(@Param("userId") String userId);

    @Select("SELECT MAX(last_synced_at) FROM owned_game WHERE user_id = #{userId}")
    java.sql.Timestamp findLastSyncedAtByUserId(@Param("userId") String userId);
}
