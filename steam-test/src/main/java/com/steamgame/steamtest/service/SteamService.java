package com.steamgame.steamtest.service;

import java.io.IOException;

/**
 * Abstract service defining Steam-related operations used by the test module.
 *
 * Implementations should call the Steam Web APIs and return raw JSON responses
 * (no parsing is performed here so the test harness can evaluate raw output).
 */
public interface SteamService {
    /**
     * Call IPlayerService/GetOwnedGames/v1 and return the raw JSON response.
     *
     * @param steamId 64-bit Steam user id
     * @param apiKey  Steam Web API key
     * @return raw JSON response as string
     */
    String getOwnedGames(String steamId, String apiKey) throws IOException, InterruptedException;

    /**
     * Call the Steam community inventory endpoint and return raw JSON.
     *
     * @param steamId   64-bit Steam user id
     * @param appId     application id (e.g. 730)
     * @param contextId inventory context id
     * @return raw JSON response as string
     */
    String getInventory(String steamId, String appId, String contextId) throws IOException, InterruptedException;
}

