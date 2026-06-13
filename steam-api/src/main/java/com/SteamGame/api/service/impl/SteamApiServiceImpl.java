package com.SteamGame.api.service.impl;

import com.SteamGame.api.domain.OwnedGame;
import com.SteamGame.api.mapper.OwnedGameMapper;
import com.SteamGame.api.service.SteamApiService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.net.http.HttpClient.Version;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

/**
 * SteamApiService 的默认实现，通过 GetSupportedAPIList 接口对 Steam Web API 密钥进行轻量级校验。
 */
@Service
public class SteamApiServiceImpl implements SteamApiService {
	private static final Logger logger = LoggerFactory.getLogger(SteamApiServiceImpl.class);
	// 使用更可控的 HttpClient：强制 HTTP/1.1、设置连接超时并允许重定向
	private final HttpClient client = HttpClient.newBuilder()
			.version(Version.HTTP_1_1)
			.followRedirects(HttpClient.Redirect.NORMAL)
			.connectTimeout(Duration.ofSeconds(10))
			.build();

	private final OwnedGameMapper ownedGameMapper;
	private final ObjectMapper objectMapper = new ObjectMapper();

	public SteamApiServiceImpl(OwnedGameMapper ownedGameMapper) {
		this.ownedGameMapper = ownedGameMapper;
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

		// 简单判断响应中是否包含 apilist 字段（为保持模块轻量化，未引入 JSON 依赖）。
		// 该接口的响应内容较小且结构固定。
		boolean contains = body.contains("\"apilist\"") || body.contains("apilist");
		logger.info("API 密钥校验 - 是否包含 'apilist': {}", contains);
		return contains;
	}

	@Override
	@Transactional
	public String getOwnedGames(String steamId, String apiKey) throws IOException, InterruptedException {
		if (steamId == null || steamId.isEmpty() || apiKey == null || apiKey.isEmpty()) {
			throw new IllegalArgumentException("steamId and apiKey are required");
		}
		String apiUrl = String.format(
				"https://api.steampowered.com/IPlayerService/GetOwnedGames/v1/?key=%s&steamid=%s&include_appinfo=1&include_played_free_games=1&format=json",
				urlEncode(apiKey), urlEncode(steamId));

		// Log the target URL to help debugging network or permission issues.
		logger.info("SteamServiceImpl: calling GetOwnedGames: {}", apiUrl);
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
				logger.info("SteamServiceImpl: GetOwnedGames HTTP status {} (attempt {})", resp.statusCode(), attempt);
				body = resp.body();
				break;
			} catch (java.io.EOFException eof) {
				// HTTP2 或 TLS 在某些环境下会抛出 EOF，尝试重试一次（降级到 HTTP/1.1 已设置）
				logger.warn("SteamServiceImpl: EOF while reading response (attempt {}), will retry once", attempt);
				if (attempt >= 2) {
					throw eof;
				}
			}
		}
		if (body == null)
			return null;

		try {
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
					try {
						// 检查是否已存在该 appid
						java.util.List<OwnedGame> existing = ownedGameMapper.findByAppid(game.getAppid());
						if (existing != null && !existing.isEmpty()) {
							ownedGameMapper.updateByAppid(game);
						} else {
							ownedGameMapper.insert(game);
						}
					} catch (Exception e) {
						logger.warn("保存 owned_game 记录失败: {}", e.getMessage());
					}
				}
			}
		} catch (Exception e) {
			logger.warn("解析或保存 OwnedGames 失败: {}", e.getMessage());
		}

		return body;
	}

	private String urlEncode(String s) {
		try {
			return URLEncoder.encode(s, StandardCharsets.UTF_8);
		} catch (Exception e) {
			return s;
		}
	}
}
