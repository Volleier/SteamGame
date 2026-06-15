package com.SteamGame.api.mapper;

import com.SteamGame.api.domain.metadata.GameCategory;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface GameCategoryMapper {

    @Insert("MERGE INTO game_category (appid, category_id, description, source, updated_at) " +
            "KEY (appid, category_id) " +
            "VALUES (#{appid}, #{categoryId}, #{description}, #{source}, #{updatedAt})")
    void upsert(GameCategory cat);

    @Select("SELECT id, appid, category_id AS categoryId, description, source, updated_at AS updatedAt " +
            "FROM game_category WHERE appid = #{appid}")
    List<GameCategory> findByAppid(@Param("appid") Long appid);

    @Delete("DELETE FROM game_category WHERE appid = #{appid}")
    void deleteByAppid(@Param("appid") Long appid);
}
