package com.SteamGame.login.service.impl;

import com.SteamGame.login.dto.CredentialDTO;
import com.SteamGame.login.dto.CredentialCheckResult;
import com.SteamGame.login.service.CredentialVerifyService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Service
public class CredentialVerifyServiceImpl implements CredentialVerifyService {

    private static final Logger logger = LoggerFactory.getLogger(CredentialVerifyServiceImpl.class);

    @Value("${login.config.path:auth.yaml}")
    private String configPath;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    // 如果需要，可在未来通过依赖注入替换为 SteamApiService

    public CredentialVerifyServiceImpl() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(4000);
        requestFactory.setReadTimeout(4000);
        this.restTemplate = new RestTemplate(requestFactory);
    }

    @Override
    public CredentialDTO readCredentialFromYaml() {
        try {
            Yaml yaml = new Yaml();
            InputStream inputStream = new FileInputStream(configPath);
            Map<String, Object> loginData = yaml.load(inputStream);

            CredentialDTO loginDTO = new CredentialDTO();
            if (loginData != null && loginData.containsKey("auth")) {
                Map<String, Object> authData = (Map<String, Object>) loginData.get("auth");
                Object steamIdObj = authData.get("steamId");
                Object apiKeyObj = authData.get("apiKey");
                Object timeObj = authData.get("time");

                String steamId = steamIdObj == null ? null : steamIdObj.toString().trim();
                String apiKey = apiKeyObj == null ? null : apiKeyObj.toString().trim();
                String time = timeObj == null ? null : timeObj.toString().trim();

                loginDTO.setSteamId(steamId);
                loginDTO.setApiKey(apiKey);
                loginDTO.setTime(time);
            }

            String loggedKey = loginDTO.getApiKey();
            String maskedKey = null;
            if (loggedKey != null) {
                String k = loggedKey.trim();
                if (k.length() > 4) {
                    maskedKey = "****" + k.substring(k.length() - 4);
                } else {
                    maskedKey = "****";
                }
            }

            logger.info("从YAML文件读取的凭据信息 - SteamID: {}, API密钥: {}",
                    loginDTO.getSteamId(), maskedKey);

            return loginDTO;
        } catch (FileNotFoundException e) {
            logger.error("未找到凭据配置文件: {}", configPath, e);
            return new CredentialDTO();
        } catch (Exception e) {
            logger.error("读取凭据配置文件时发生错误", e);
            return new CredentialDTO();
        }
    }

    @Override
    public ResponseEntity<CredentialDTO> sendCredentialInfoToFrontend() {
        CredentialDTO loginDTO = readCredentialFromYaml();
        logger.info("准备发送到前端的凭据信息 - SteamID: {}, API密钥: {}",
                loginDTO.getSteamId(), loginDTO.getApiKey());
        return ResponseEntity.ok(loginDTO);
    }

    @Override
    public ResponseEntity<CredentialCheckResult> validateCredential() {
        CredentialDTO loginDTO = readCredentialFromYaml();
        String key = loginDTO.getApiKey();
        String steamid = loginDTO.getSteamId();

        if (key == null || key.isEmpty() || steamid == null || steamid.isEmpty()) {
            CredentialCheckResult result = new CredentialCheckResult(false, false, false, "请求中缺少steamId或apiKey", null);
            return ResponseEntity.badRequest().body(result);
        }

        try {
            Integer gameCount = null;
            // 回退：使用 RestTemplate 直接调用 Steam Web API
            {
                String summariesUrl = String.format(
                        "https://api.steampowered.com/ISteamUser/GetPlayerSummaries/v2/?key=%s&steamids=%s", key,
                        steamid);
                String summariesBody = restTemplate.getForObject(summariesUrl, String.class);
                JsonNode summariesJson = objectMapper.readTree(summariesBody);
                JsonNode players = summariesJson.path("response").path("players");
                boolean playerExists = players.isArray() && players.size() == 1;
                if (!playerExists) {
                    CredentialCheckResult result = new CredentialCheckResult(false, false, false,
                            "Invalid steamId or apiKey (player not found)", null);
                    return ResponseEntity.status(404).body(result);
                }

                String ownedUrl = String.format(
                        "https://api.steampowered.com/IPlayerService/GetOwnedGames/v1/?key=%s&steamid=%s&include_played_free_games=1&format=json",
                        key, steamid);
                String ownedBody = restTemplate.getForObject(ownedUrl, String.class);
                logger.info("getOwnedGames response (via RestTemplate): {}", ownedBody);
                JsonNode ownedJson = objectMapper.readTree(ownedBody);
                JsonNode responseNode = ownedJson.path("response");
                if (responseNode.has("game_count")) {
                    gameCount = responseNode.path("game_count").asInt(0);
                } else if (responseNode.has("games") && responseNode.path("games").isArray()) {
                    gameCount = responseNode.path("games").size();
                } else {
                    gameCount = 0;
                }
            }

            boolean ownsGames = gameCount != null && gameCount > 0;
            boolean profilePrivate = (gameCount == null || gameCount == 0);

            String message = "验证成功";
            if (profilePrivate) {
                message = "玩家存在但未返回拥有的游戏（可能为隐私或无游戏）";
            }

            CredentialCheckResult result = new CredentialCheckResult(true, ownsGames, profilePrivate, message,
                    gameCount);
            return ResponseEntity.ok(result);

        } catch (IOException e) {
            logger.error("使用 Steam API 验证 API 密钥时出错", e);
            CredentialCheckResult result = new CredentialCheckResult(false, false, false,
                    "验证 API 密钥时出错: " + e.getMessage(),
                    null);
            return ResponseEntity.status(502).body(result);
        } catch (RestClientException e) {
            logger.error("调用 Steam API 时网络错误", e);
            CredentialCheckResult result = new CredentialCheckResult(false, false, false,
                    "调用 Steam API 时网络错误: " + e.getMessage(),
                    null);
            return ResponseEntity.status(502).body(result);
        } catch (Exception e) {
            logger.error("处理 Steam API 响应时出错", e);
            CredentialCheckResult result = new CredentialCheckResult(false, false, false,
                    "解析 Steam API 响应时出错: " + e.getMessage(),
                    null);
            return ResponseEntity.status(500).body(result);
        }
    }
}
