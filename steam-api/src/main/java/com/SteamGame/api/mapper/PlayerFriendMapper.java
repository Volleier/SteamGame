package com.SteamGame.api.mapper;

import com.SteamGame.api.domain.player.PlayerFriend;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PlayerFriendMapper {

    @Insert("MERGE INTO player_friend (user_id, friend_steam_id, relationship, friend_since, synced_at) " +
            "KEY (user_id, friend_steam_id) " +
            "VALUES (#{userId}, #{friendSteamId}, #{relationship}, #{friendSince}, #{syncedAt})")
    void upsert(PlayerFriend friend);

    @Select("SELECT id, user_id AS userId, friend_steam_id AS friendSteamId, " +
            "relationship, friend_since AS friendSince, synced_at AS syncedAt " +
            "FROM player_friend WHERE user_id = #{userId}")
    List<PlayerFriend> findByUserId(@Param("userId") String userId);
}
