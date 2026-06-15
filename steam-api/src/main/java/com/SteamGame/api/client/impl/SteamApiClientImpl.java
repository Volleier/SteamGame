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

        logger.info("SteamApiClient: calling GetOwnedGames for steamId={}", steamId);
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

        if (resp == null || body == null) {
            logger.error("SteamApiClient: GetOwnedGames failed — no response received");
            return new ArrayList<>();
        }

        if (resp.statusCode() != 200) {
            logger.error("SteamApiClient: GetOwnedGames returned HTTP {} (expected 200)", resp.statusCode());
            throw new IOException("Steam API returned HTTP " + resp.statusCode());
        }

        return parseGamesFromJson(body);
    }

    /**
     * 从 Steam API 响应 JSON 中解析游戏列表。
     * 只负责解析 Steam 返回字段 (appid/name/playtime_forever)，
     * userId、steamId、lastSyncedAt 等归属字段由业务服务统一设置。
     *
     * @param body Steam API 原始响应 JSON
     * @return 解析后的游戏列表（仅含 Steam 原始字段）
     */
    public List<OwnedGame> parseGamesFromJson(String body) throws IOException {
        List<OwnedGame> gameList = new ArrayList<>();
        JsonNode root = objectMapper.readTree(body);
        // 响应结构：{ response: { games: [ { appid, name, playtime_forever }, ... ] } }
        JsonNode responseNode = root.path("response");
        JsonNode gamesNode = responseNode.path("games");

        if (gamesNode.isArray()) {
            Iterator<JsonNode> it = gamesNode.elements();
            while (it.hasNext()) {
                JsonNode g = it.next();
                OwnedGame game = new OwnedGame();
                game.setAppid(g.path("appid").asLong());
                game.setName(g.path("name").asText(null));
                game.setPlaytimeForever(g.path("playtime_forever").asInt(0));
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

    @Override
    public void fillGameDetails(OwnedGame game) {
        String url = String.format("https://store.steampowered.com/api/appdetails?appids=%d&l=zh-cn", game.getAppid());
        logger.info("SteamApiClient: calling store appdetails: {}", url);
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .timeout(Duration.ofSeconds(6))
                    .build();
            HttpResponse<String> resp = client.send(req, BodyHandlers.ofString());
            if (resp.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(resp.body());
                JsonNode appNode = root.path(String.valueOf(game.getAppid()));
                if (appNode.path("success").asBoolean()) {
                    JsonNode dataNode = appNode.path("data");
                    
                    // 解析 developers
                    JsonNode devs = dataNode.path("developers");
                    if (devs.isArray() && devs.size() > 0) {
                        game.setDeveloper(devs.get(0).asText());
                    }
                    
                    // 解析 publishers
                    JsonNode pubs = dataNode.path("publishers");
                    if (pubs.isArray() && pubs.size() > 0) {
                        game.setPublisher(pubs.get(0).asText());
                    }

                    // 解析 release_date
                    JsonNode relDateNode = dataNode.path("release_date");
                    if (relDateNode.isObject()) {
                        game.setReleaseDate(relDateNode.path("date").asText());
                    }

                    // 解析 categories 和 genres 作为 tags 逗号拼接存入
                    java.util.List<String> tagList = new java.util.ArrayList<>();
                    JsonNode categories = dataNode.path("categories");
                    if (categories.isArray()) {
                        for (JsonNode cat : categories) {
                            String desc = cat.path("description").asText();
                            if (!desc.isEmpty()) {
                                tagList.add(desc);
                            }
                        }
                    }
                    JsonNode genres = dataNode.path("genres");
                    if (genres.isArray()) {
                        for (JsonNode gen : genres) {
                            String desc = gen.path("description").asText();
                            if (!desc.isEmpty() && !tagList.contains(desc)) {
                                tagList.add(desc);
                            }
                        }
                    }
                    if (!tagList.isEmpty()) {
                        game.setTags(String.join(",", tagList));
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to fetch game details for appid {}: {}", game.getAppid(), e.getMessage());
        }
    }
}
