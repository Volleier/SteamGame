package com.SteamGame.login.service.impl;

import com.SteamGame.login.dto.CredentialCheckResult;
import com.SteamGame.login.service.CredentialValidationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.SocketTimeoutException;

@Service
public class CredentialValidationServiceImpl implements CredentialValidationService {

    private static final Logger logger = LoggerFactory.getLogger(CredentialValidationServiceImpl.class);
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CredentialValidationServiceImpl() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5000);
        requestFactory.setReadTimeout(5000);
        this.restTemplate = new RestTemplate(requestFactory);
    }

    @Override
    public CredentialCheckResult validateOnline(String steamId, String plainApiKey) throws Exception {
        // Step 1: GetPlayerSummaries
        boolean playerExists;
        try {
            String summariesUrl = String.format(
                    "https://api.steampowered.com/ISteamUser/GetPlayerSummaries/v2/?key=%s&steamids=%s",
                    plainApiKey, steamId);
            String summariesBody = restTemplate.getForObject(summariesUrl, String.class);
            JsonNode summariesJson = objectMapper.readTree(summariesBody);
            JsonNode players = summariesJson.path("response").path("players");
            playerExists = players.isArray() && players.size() == 1;
        } catch (ResourceAccessException e) {
            if (e.getCause() instanceof SocketTimeoutException) {
                logger.error("Steam API 超时: GetPlayerSummaries", e);
                throw new Exception("STEAM_API_TIMEOUT", e);
            }
            logger.error("Steam API 不可用: GetPlayerSummaries", e);
            throw new Exception("STEAM_API_UNAVAILABLE", e);
        } catch (RestClientException e) {
            logger.error("Steam API 调用失败: GetPlayerSummaries", e);
            throw new Exception("STEAM_API_UNAVAILABLE", e);
        }

        if (!playerExists) {
            return new CredentialCheckResult(false, false, false, "apiKey 或 SteamID 无效", 0);
        }

        // Step 2: GetOwnedGames
        try {
            String ownedUrl = String.format(
                    "https://api.steampowered.com/IPlayerService/GetOwnedGames/v1/?key=%s&steamid=%s&include_played_free_games=1&format=json",
                    plainApiKey, steamId);
            String ownedBody = restTemplate.getForObject(ownedUrl, String.class);
            logger.debug("getOwnedGames response: {}", ownedBody);
            JsonNode ownedJson = objectMapper.readTree(ownedBody);
            JsonNode responseNode = ownedJson.path("response");

            Integer gameCount;
            if (responseNode.has("game_count")) {
                gameCount = responseNode.path("game_count").asInt(0);
            } else if (responseNode.has("games") && responseNode.path("games").isArray()) {
                gameCount = responseNode.path("games").size();
            } else {
                gameCount = 0;
            }

            boolean ownsGames = gameCount != null && gameCount > 0;
            boolean profilePrivate = (gameCount == null || gameCount == 0);
            String message = profilePrivate ? "玩家存在但未返回拥有的游戏（可能为隐私或无游戏）" : "验证成功";
            return new CredentialCheckResult(true, ownsGames, profilePrivate, message, gameCount);

        } catch (ResourceAccessException e) {
            if (e.getCause() instanceof SocketTimeoutException) {
                logger.error("Steam API 超时: GetOwnedGames", e);
                throw new Exception("STEAM_API_TIMEOUT", e);
            }
            logger.error("Steam API 不可用: GetOwnedGames", e);
            throw new Exception("STEAM_API_UNAVAILABLE", e);
        } catch (RestClientException e) {
            logger.error("Steam API 调用失败: GetOwnedGames", e);
            throw new Exception("STEAM_API_UNAVAILABLE", e);
        }
    }
}
