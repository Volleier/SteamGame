package com.SteamGame.api.service.impl;

import com.SteamGame.api.client.SteamApiClient;
import com.SteamGame.api.domain.OwnedGame;
import com.SteamGame.api.mapper.OwnedGameMapper;
import com.SteamGame.api.service.SteamApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

/**
 * SteamApiService 的默认实现。
 * <p>
 * 游戏库同步的 HTTP 请求已委托给 {@link SteamApiClient}，
 * 本类负责将游戏列表持久化到本地数据库。
 */
@Service
public class SteamApiServiceImpl implements SteamApiService {
    private static final Logger logger = LoggerFactory.getLogger(SteamApiServiceImpl.class);

    private final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final OwnedGameMapper ownedGameMapper;
    private final SteamApiClient steamApiClient;

    public SteamApiServiceImpl(OwnedGameMapper ownedGameMapper, SteamApiClient steamApiClient) {
        this.ownedGameMapper = ownedGameMapper;
        this.steamApiClient = steamApiClient;
    }

    @Override
    public String getSupportedApiList(String apiKey) throws IOException, InterruptedException {
        if (apiKey == null || apiKey.isEmpty())
            return null;
        String url = String.format("https://api.steampowered.com/ISteamWebAPIUtil/GetSupportedAPIList/v1/?key=%s",
                urlEncode(apiKey));
        logger.info("请求支持的 API 列表: {}", url);
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        HttpResponse<String> resp = client.send(req, BodyHandlers.ofString());
        logger.info("GetSupportedAPIList 状态码: {}", resp.statusCode());
        return resp.body();
    }

    @Override
    public boolean isApiKeyValid(String apiKey) throws IOException, InterruptedException {
        if (apiKey == null || apiKey.isEmpty())
            return false;
        HttpResponse<String> resp = client.send(HttpRequest.newBuilder()
                .uri(URI.create(
                        String.format("https://api.steampowered.com/ISteamWebAPIUtil/GetSupportedAPIList/v1/?key=%s",
                                urlEncode(apiKey))))
                .GET().build(), BodyHandlers.ofString());

        if (resp.statusCode() != 200) {
            logger.warn("API 密钥校验返回非 200 状态码: {}", resp.statusCode());
            return false;
        }

        String body = resp.body();
        if (body == null)
            return false;

        boolean contains = body.contains("\"apilist\"") || body.contains("apilist");
        logger.info("API 密钥校验 - 是否包含 'apilist': {}", contains);
        return contains;
    }

    @Override
    @Transactional
    public String getOwnedGames(String steamId, String apiKey) throws IOException, InterruptedException {
        // 委托 SteamApiClient 完成 HTTP 请求和 JSON 解析
        List<OwnedGame> games = steamApiClient.fetchOwnedGames(steamId, apiKey);

        // 使用 MERGE INTO upsert 写入本地数据库
        for (OwnedGame game : games) {
            try {
                ownedGameMapper.upsert(game);
            } catch (Exception e) {
                logger.warn("upsert owned_game 失败 (appid={}): {}", game.getAppid(), e.getMessage());
            }
        }

        logger.info("SteamApiServiceImpl: synced {} games to database", games.size());
        return games.size() + " games synced";
    }

    private String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return s;
        }
    }
}
