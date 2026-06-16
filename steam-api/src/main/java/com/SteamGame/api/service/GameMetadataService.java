package com.SteamGame.api.service;

import com.SteamGame.api.dto.metadata.GameMetadataDTO;
import com.SteamGame.api.dto.metadata.GameMetadataSyncResultDTO;

public interface GameMetadataService {

    /**
     * Get metadata for a single game, from cache or Store API.
     */
    GameMetadataDTO getMetadata(Long appid);

    /**
     * Force-sync metadata for a single game from Store API.
     */
    GameMetadataDTO syncMetadata(Long appid, String language, String countryCode);

    /**
     * Batch sync missing metadata for games owned by a user.
     */
    GameMetadataSyncResultDTO syncMissing(String userId, int limit);
}
