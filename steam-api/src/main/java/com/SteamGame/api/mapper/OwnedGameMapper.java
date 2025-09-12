package com.SteamGame.api.mapper;

import com.SteamGame.api.domain.OwnedGame;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OwnedGameMapper {

    @Insert("INSERT INTO owned_game(appid, name, playtime_forever) VALUES(#{appid}, #{name}, #{playtimeForever})")
    void insert(OwnedGame game);

    // 用于检查表是否存在
    @Select("SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'OWNED_GAME'")
    Integer countTable();

    // 创建表
    @Insert("CREATE TABLE IF NOT EXISTS owned_game( id BIGINT AUTO_INCREMENT PRIMARY KEY, appid BIGINT, name VARCHAR(512), playtime_forever INT )")
    void createTableIfNotExists();

    // 根据 appid 查询游戏
    @Select("SELECT id, appid, name, playtime_forever AS playtimeForever FROM owned_game WHERE appid = #{appid}")
    List<OwnedGame> findByAppid(@Param("appid") Long appid);

    // 根据 appid 更新游戏记录
    @org.apache.ibatis.annotations.Update("UPDATE owned_game SET name = #{name}, playtime_forever = #{playtimeForever} WHERE appid = #{appid}")
    void updateByAppid(OwnedGame game);

    @Select("SELECT id, appid, name, playtime_forever AS playtimeForever FROM owned_game")
    List<OwnedGame> listAll();
}
