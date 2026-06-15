package com.SteamGame.api.client.store;

import com.SteamGame.api.dto.metadata.GameMetadataDTO;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Steam Store API client — calls store.steampowered.com/api endpoints.
 * Responsible for HTTP requests and JSON parsing only, NOT database operations.
 */
public interface SteamStoreClient {

    /**
     * Store AppDetails for a single appid.
     */
    GameMetadataDTO getAppDetails(long appid, String language, String countryCode) throws IOException, InterruptedException;

    /**
     * Batch Store AppDetails for multiple appids.
     * Default implementation iterates with delay to avoid rate limiting.
     */
    Map<Long, GameMetadataDTO> getAppDetailsBatch(List<Long> appids, String language, String countryCode) throws IOException, InterruptedException;
}
