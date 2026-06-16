package com.SteamGame.api.mapper;

import com.SteamGame.api.domain.metadata.GameMetadata;
import org.apache.ibatis.annotations.*;

@Mapper
public interface GameMetadataMapper {

    @Insert("MERGE INTO game_metadata (appid, name, type, short_description, detailed_description, " +
            "header_image, capsule_image, website, developers, publishers, release_date, coming_soon, " +
            "platform_windows, platform_mac, platform_linux, price_currency, price_initial, price_final, " +
            "discount_percent, metacritic_score, pc_requirements, metadata_source, metadata_synced_at, " +
            "created_at, updated_at) " +
            "KEY (appid) " +
            "VALUES (#{appid}, #{name}, #{type}, #{shortDescription}, #{detailedDescription}, " +
            "#{headerImage}, #{capsuleImage}, #{website}, #{developers}, #{publishers}, #{releaseDate}, #{comingSoon}, " +
            "#{platformWindows}, #{platformMac}, #{platformLinux}, #{priceCurrency}, #{priceInitial}, #{priceFinal}, " +
            "#{discountPercent}, #{metacriticScore}, #{pcRequirements}, #{metadataSource}, #{metadataSyncedAt}, " +
            "#{createdAt}, #{updatedAt})")
    void upsert(GameMetadata meta);

    @Select("SELECT appid, name, type, short_description AS shortDescription, " +
            "detailed_description AS detailedDescription, header_image AS headerImage, " +
            "capsule_image AS capsuleImage, website, developers, publishers, " +
            "release_date AS releaseDate, coming_soon AS comingSoon, " +
            "platform_windows AS platformWindows, platform_mac AS platformMac, " +
            "platform_linux AS platformLinux, price_currency AS priceCurrency, " +
            "price_initial AS priceInitial, price_final AS priceFinal, " +
            "discount_percent AS discountPercent, metacritic_score AS metacriticScore, " +
            "pc_requirements AS pcRequirements, metadata_source AS metadataSource, " +
            "metadata_synced_at AS metadataSyncedAt, created_at AS createdAt, updated_at AS updatedAt " +
            "FROM game_metadata WHERE appid = #{appid}")
    GameMetadata findByAppid(@Param("appid") Long appid);

    @Select("SELECT appid FROM owned_game WHERE user_id = #{userId} " +
            "AND appid NOT IN (SELECT appid FROM game_metadata) LIMIT #{limit}")
    java.util.List<Long> findAppidsMissingMetadata(@Param("userId") String userId, @Param("limit") int limit);
}
