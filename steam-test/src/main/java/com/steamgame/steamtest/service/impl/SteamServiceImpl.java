package com.steamgame.steamtest.service.impl;

import com.steamgame.steamtest.service.SteamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;

@Service
/**
 * Default implementation of the SteamService interface.
 *
 * This class performs synchronous HTTP calls using Java 11+ HttpClient and
 * returns raw JSON strings. Error handling is intentionally minimal so that
 * the test harness can inspect failures directly.
 */
public class SteamServiceImpl implements SteamService {
    private static final Logger logger = LoggerFactory.getLogger(SteamServiceImpl.class);
    private final HttpClient client = HttpClient.newHttpClient();

    @Override
    public String getOwnedGames(String steamId, String apiKey) throws IOException, InterruptedException {
        if (steamId == null || steamId.isEmpty() || apiKey == null || apiKey.isEmpty()) {
            throw new IllegalArgumentException("steamId and apiKey are required");
        }
        String apiUrl = String.format(
                "https://api.steampowered.com/IPlayerService/GetOwnedGames/v1/?key=%s&steamid=%s&include_appinfo=1&include_played_free_games=1&format=json",
                urlEncode(apiKey), urlEncode(steamId));

        // Log the target URL to help debugging network or permission issues.
        logger.info("SteamServiceImpl: calling GetOwnedGames: {}", apiUrl);
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(apiUrl)).GET().build();
        HttpResponse<String> resp = client.send(req, BodyHandlers.ofString());
        logger.info("SteamServiceImpl: GetOwnedGames HTTP status {}", resp.statusCode());
        return resp.body();
    }

    @Override
    public String getInventory(String steamId, String appId, String contextId) throws IOException, InterruptedException {
        if (steamId == null || steamId.isEmpty() || appId == null || contextId == null) {
            throw new IllegalArgumentException("steamId, appId and contextId are required");
        }
        String url = String.format("https://steamcommunity.com/inventory/%s/%s/%s?l=english&count=5000", urlEncode(steamId), urlEncode(appId), urlEncode(contextId));
        logger.info("SteamServiceImpl: calling inventory endpoint: {}", url);
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        HttpResponse<String> resp = client.send(req, BodyHandlers.ofString());
        logger.info("SteamServiceImpl: inventory HTTP status {}", resp.statusCode());
        return resp.body();
    }

    private String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, StandardCharsets.UTF_8);
        } catch (Exception e) {
            // On encode failure return the original string - caller will likely see an error
            return s;
        }
    }
}
