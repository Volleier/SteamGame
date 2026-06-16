package com.SteamGame.api.mapper;

import com.SteamGame.api.domain.news.GameNews;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface GameNewsMapper {

    @Insert("MERGE INTO game_news (gid, appid, title, url, external_url, author, contents, feed_label, date, synced_at) " +
            "KEY (gid) " +
            "VALUES (#{gid}, #{appid}, #{title}, #{url}, #{externalUrl}, #{author}, #{contents}, #{feedLabel}, #{date}, #{syncedAt})")
    void upsert(GameNews news);

    @Select("SELECT gid, appid, title, url, external_url AS externalUrl, author, contents, " +
            "feed_label AS feedLabel, date, synced_at AS syncedAt " +
            "FROM game_news WHERE appid = #{appid} ORDER BY date DESC LIMIT #{limit}")
    List<GameNews> findByAppid(@Param("appid") Long appid, @Param("limit") int limit);
}
