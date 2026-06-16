package com.SteamGame.api.mapper;

import com.SteamGame.api.domain.player.PlayerProfile;
import org.apache.ibatis.annotations.*;

@Mapper
public interface PlayerProfileMapper {

    @Insert("MERGE INTO player_profile (user_id, steam_id, persona_name, profile_url, " +
            "avatar, avatar_medium, avatar_full, persona_state, community_visibility_state, " +
            "last_logoff, time_created, country_code, synced_at, updated_at) " +
            "KEY (user_id) " +
            "VALUES (#{userId}, #{steamId}, #{personaName}, #{profileUrl}, " +
            "#{avatar}, #{avatarMedium}, #{avatarFull}, #{personaState}, #{communityVisibilityState}, " +
            "#{lastLogoff}, #{timeCreated}, #{countryCode}, #{syncedAt}, #{updatedAt})")
    void upsert(PlayerProfile profile);

    @Select("SELECT user_id AS userId, steam_id AS steamId, persona_name AS personaName, " +
            "profile_url AS profileUrl, avatar, avatar_medium AS avatarMedium, " +
            "avatar_full AS avatarFull, persona_state AS personaState, " +
            "community_visibility_state AS communityVisibilityState, last_logoff AS lastLogoff, " +
            "time_created AS timeCreated, country_code AS countryCode, " +
            "synced_at AS syncedAt, updated_at AS updatedAt " +
            "FROM player_profile WHERE user_id = #{userId}")
    PlayerProfile findByUserId(@Param("userId") String userId);
}
