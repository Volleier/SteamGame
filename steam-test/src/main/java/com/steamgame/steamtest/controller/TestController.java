package com.steamgame.steamtest.controller;

import lombok.Data;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.steamgame.steamtest.service.SteamService;

@Component
/**
 * Internal test component for exercising Steam API calls inside the application.
 *
 * This component is not exposed as an external HTTP controller. It provides
 * methods for invoking Steam-related operations via the service layer and is
 * intended for internal use (startup tasks, tests, or manual invocations).
 */
public class TestController {
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @Qualifier("steamServiceImpl")
    @Autowired
    private SteamService steamService;

    /**
     * Log the supplied SteamRequest for debugging and verification.
     * This is an internal method and not mapped to any HTTP endpoint.
     *
     * @param request internal request carrying steamId and apiKey
     * @return acknowledgement string
     */
    public String receiveSteamData(SteamRequest request) {
        logger.info("Received internal SteamRequest");
        logger.info("Steam ID: {}", request.getSteamId());
        logger.info("API Key: {}", request.getApiKey());
        return "data received";
    }

    /**
     * Internal DTO for passing Steam authentication data between beans.
     * Uses Lombok (@Data) to generate boilerplate accessors.
     */
    @Data
    public static class SteamRequest {
        private String steamId;
        private String apiKey;
    }
    /**
     * Invoke SteamService.getOwnedGames and return raw JSON.
     *
     * @param steamId Steam user's 64-bit id
     * @param apiKey  Steam Web API key
     * @return raw JSON response or null on error
     */
    public String ownedGamesInternal(String steamId, String apiKey) {
        try {
            return steamService.getOwnedGames(steamId, apiKey);
        } catch (Exception e) {
            logger.error("ownedGamesInternal call failed: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Invoke SteamService.getInventory and return raw JSON.
     *
     * @param steamId   Steam user's 64-bit id
     * @param appId     application id (e.g., 730)
     * @param contextId inventory context id
     * @return raw JSON response or null on error
     */
    public String inventoryInternal(String steamId, String appId, String contextId) {
        try {
            return steamService.getInventory(steamId, appId, contextId);
        } catch (Exception e) {
            logger.error("inventoryInternal call failed: {}", e.getMessage(), e);
            return null;
        }
    }
}