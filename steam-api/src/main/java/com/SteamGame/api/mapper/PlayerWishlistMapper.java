package com.SteamGame.api.mapper;

import com.SteamGame.api.domain.player.PlayerWishlist;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PlayerWishlistMapper {

    @Insert("MERGE INTO player_wishlist (user_id, appid, name, priority, added_at, synced_at) " +
            "KEY (user_id, appid) " +
            "VALUES (#{userId}, #{appid}, #{name}, #{priority}, #{addedAt}, #{syncedAt})")
    void upsert(PlayerWishlist item);

    @Select("SELECT id, user_id AS userId, appid, name, priority, added_at AS addedAt, synced_at AS syncedAt " +
            "FROM player_wishlist WHERE user_id = #{userId} ORDER BY priority ASC, added_at DESC")
    List<PlayerWishlist> findByUserId(@Param("userId") String userId);
}
