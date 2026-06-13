package com.SteamGame.api.client.impl;

import com.SteamGame.api.client.SteamApiClient;
import com.SteamGame.api.domain.OwnedGame;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * SteamApiClient 默认实现 —— 只负责 HTTP 请求和 JSON 解析，不操作数据库。
 */
@Component
public class SteamApiClientImpl implements SteamApiClient {

    private static final Logger logger = LoggerFactory.getLogger(SteamApiClientImpl.class);

    private final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<OwnedGame> fetchOwnedGames(String steamId, String apiKey) throws IOException, InterruptedException {
        if (steamId == null || steamId.isEmpty() || apiKey == null || apiKey.isEmpty()) {
            throw new IllegalArgumentException("steamId and apiKey are required");
        }

        String apiUrl = String.format(
                "https://api.steampowered.com/IPlayerService/GetOwnedGames/v1/?key=%s&steamid=%s&include_appinfo=1&include_played_free_games=1&format=json",
                urlEncode(apiKey), urlEncode(steamId));

        logger.info("SteamApiClient: calling GetOwnedGames: {}", apiUrl);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .GET()
                .header("User-Agent", "SteamGameClient/1.0")
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(15))
                .build();

        HttpResponse<String> resp = null;
        String body = null;
        int attempt = 0;
        while (attempt < 2) {
            attempt++;
            try {
                resp = client.send(req, BodyHandlers.ofString());
                logger.info("SteamApiClient: GetOwnedGames HTTP status {} (attempt {})", resp.statusCode(), attempt);
                body = resp.body();
                break;
            } catch (java.io.EOFException eof) {
                logger.warn("SteamApiClient: EOF while reading response (attempt {}), will retry once", attempt);
                if (attempt >= 2) {
                    throw eof;
                }
            }
        }

        if (body == null) {
            return new ArrayList<>();
        }

        return parseGamesFromJson(body, steamId);
    }

    /**
     * 从 Steam API 响应 JSON 中解析游戏列表。
     *
     * @param body    Steam API 原始响应 JSON
     * @param steamId 请求时使用的 64 位 Steam 用户 ID
     * @return 解析后的游戏列表
     */
    List<OwnedGame> parseGamesFromJson(String body, String steamId) throws IOException {
        List<OwnedGame> gameList = new ArrayList<>();
        JsonNode root = objectMapper.readTree(body);
        // 响应结构：{ response: { games: [ { appid, name, playtime_forever }, ... ] } }
        JsonNode responseNode = root.path("response");
        JsonNode gamesNode = responseNode.path("games");
        java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());

        if (gamesNode.isArray()) {
            Iterator<JsonNode> it = gamesNode.elements();
            while (it.hasNext()) {
                JsonNode g = it.next();
                OwnedGame game = new OwnedGame();
                game.setUserId("default");
                game.setSteamId(steamId);
                game.setAppid(g.path("appid").asLong());
                game.setName(g.path("name").asText(null));
                game.setPlaytimeForever(g.path("playtime_forever").asInt(0));
                game.setLastSyncedAt(now);
                gameList.add(game);
            }
        }

        logger.info("SteamApiClient: parsed {} games from response", gameList.size());
        return gameList;
    }

    private String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return s;
        }
    }
}
