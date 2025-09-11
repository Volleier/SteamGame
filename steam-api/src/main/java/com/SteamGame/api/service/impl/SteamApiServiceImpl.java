package com.SteamGame.api.service.impl;

import com.SteamGame.api.service.SteamApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * SteamApiService 的默认实现，通过 GetSupportedAPIList 接口对 Steam Web API 密钥进行轻量级校验。
 */
@Service
public class SteamApiServiceImpl implements SteamApiService {
	private static final Logger logger = LoggerFactory.getLogger(SteamApiServiceImpl.class);
	private final HttpClient client = HttpClient.newHttpClient();

	@Override
	public String getSupportedApiList(String apiKey) throws IOException, InterruptedException {
		if (apiKey == null || apiKey.isEmpty()) return null;
		String url = String.format("https://api.steampowered.com/ISteamWebAPIUtil/GetSupportedAPIList/v1/?key=%s", urlEncode(apiKey));
		logger.info("请求支持的 API 列表: {}", url);
		HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
		HttpResponse<String> resp = client.send(req, BodyHandlers.ofString());
		logger.info("GetSupportedAPIList 状态码: {}", resp.statusCode());
		return resp.body();
	}

	@Override
	public boolean isApiKeyValid(String apiKey) throws IOException, InterruptedException {
		if (apiKey == null || apiKey.isEmpty()) return false;
		HttpResponse<String> resp = client.send(HttpRequest.newBuilder()
				.uri(URI.create(String.format("https://api.steampowered.com/ISteamWebAPIUtil/GetSupportedAPIList/v1/?key=%s", urlEncode(apiKey))))
				.GET().build(), BodyHandlers.ofString());

		if (resp.statusCode() != 200) {
			logger.warn("API 密钥校验返回非 200 状态码: {}", resp.statusCode());
			return false;
		}

		String body = resp.body();
		if (body == null) return false;

		// 简单判断响应中是否包含 apilist 字段（为保持模块轻量化，未引入 JSON 依赖）。
		// 该接口的响应内容较小且结构固定。
		boolean contains = body.contains("\"apilist\"") || body.contains("apilist");
		logger.info("API 密钥校验 - 是否包含 'apilist': {}", contains);
		return contains;
	}


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

	private String urlEncode(String s) {
		try {
			return URLEncoder.encode(s, StandardCharsets.UTF_8);
		} catch (Exception e) {
			return s;
		}
	}
}
