package com.SteamGame.api.mapper;

import com.SteamGame.api.domain.metadata.GameGenre;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface GameGenreMapper {

    @Insert("MERGE INTO game_genre (appid, genre_id, description, source, updated_at) " +
            "KEY (appid, genre_id) " +
            "VALUES (#{appid}, #{genreId}, #{description}, #{source}, #{updatedAt})")
    void upsert(GameGenre genre);

    @Select("SELECT id, appid, genre_id AS genreId, description, source, updated_at AS updatedAt " +
            "FROM game_genre WHERE appid = #{appid}")
    List<GameGenre> findByAppid(@Param("appid") Long appid);

    @Delete("DELETE FROM game_genre WHERE appid = #{appid}")
    void deleteByAppid(@Param("appid") Long appid);
}
