package com.SteamGame.login.service.impl;

import com.SteamGame.login.dto.LoginDTO;
import com.SteamGame.login.dto.LoginCheckResult;
import com.SteamGame.login.service.LoginService;
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
import java.io.InputStream;
import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {

    private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

    @Value("${login.config.path:auth.yaml}")
    private String configPath;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LoginServiceImpl() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(4000);
        requestFactory.setReadTimeout(4000);
        this.restTemplate = new RestTemplate(requestFactory);
    }

    @Override
    public LoginDTO readLoginInfoFromYaml() {
        try {
            Yaml yaml = new Yaml();
            InputStream inputStream = new FileInputStream(configPath);
            Map<String, Object> loginData = yaml.load(inputStream);

            LoginDTO loginDTO = new LoginDTO();
            if (loginData != null && loginData.containsKey("auth")) {
                Map<String, Object> authData = (Map<String, Object>) loginData.get("auth");
                loginDTO.setSteamId((String) authData.get("steamId"));
                loginDTO.setApiKey((String) authData.get("apiKey"));
                loginDTO.setTime((String) authData.get("time"));
            }

            logger.info("从YAML文件读取的登录信息 - SteamID: {}, API密钥: {}",
                    loginDTO.getSteamId(), loginDTO.getApiKey());

            return loginDTO;
        } catch (FileNotFoundException e) {
            logger.error("未找到登录配置文件: {}", configPath, e);
            return new LoginDTO();
        } catch (Exception e) {
            logger.error("读取登录配置文件时发生错误", e);
            return new LoginDTO();
        }
    }

    @Override
    public ResponseEntity<LoginDTO> sendLoginInfoToFrontend() {
        LoginDTO loginDTO = readLoginInfoFromYaml();
        logger.info("准备发送到前端的登录信息 - SteamID: {}, API密钥: {}",
                loginDTO.getSteamId(), loginDTO.getApiKey());
        return ResponseEntity.ok(loginDTO);
    }

    @Override
    public ResponseEntity<LoginCheckResult> validateLogin() {
        LoginDTO loginDTO = readLoginInfoFromYaml();
        String key = loginDTO.getApiKey();
        String steamid = loginDTO.getSteamId();

        if (key == null || key.isEmpty() || steamid == null || steamid.isEmpty()) {
            LoginCheckResult result = new LoginCheckResult(false, false, false, "请求中缺少steamId或apiKey", null);
            return ResponseEntity.badRequest().body(result); // 400
        }

        try {
            String summariesUrl = String.format("https://api.steampowered.com/ISteamUser/GetPlayerSummaries/v2/?key=%s&steamids=%s", key, steamid);
            String summariesBody = restTemplate.getForObject(summariesUrl, String.class);
            JsonNode summariesJson = objectMapper.readTree(summariesBody);
            JsonNode players = summariesJson.path("response").path("players");
            boolean playerExists = players.isArray() && players.size() == 1;

            if (!playerExists) {
                LoginCheckResult result = new LoginCheckResult(false, false, false, "无效的steamId或apiKey（未找到玩家）", null);
                return ResponseEntity.status(404).body(result); // 404 Not Found
            }

            String ownedUrl = String.format("https://api.steampowered.com/IPlayerService/GetOwnedGames/v1/?key=%s&steamid=%s&include_played_free_games=1&format=json", key, steamid);
            String ownedBody = restTemplate.getForObject(ownedUrl, String.class);
            JsonNode ownedJson = objectMapper.readTree(ownedBody);
            JsonNode responseNode = ownedJson.path("response");
            Integer gameCount = null;
            if (responseNode.has("game_count")) {
                gameCount = responseNode.path("game_count").asInt(0);
            }

            boolean ownsGames = gameCount != null && gameCount > 0;
            boolean profilePrivate = (gameCount == null || gameCount == 0);

            String message = "验证成功";
            if (profilePrivate) {
                message = "玩家存在但未返回拥有的游戏（可能为隐私或无游戏）";
            }

            LoginCheckResult result = new LoginCheckResult(true, ownsGames, profilePrivate, message, gameCount);
            return ResponseEntity.ok(result); // 200

        } catch (RestClientException e) {
            logger.error("调用Steam API时发生网络错误", e);
            LoginCheckResult result = new LoginCheckResult(false, false, false, "调用Steam API时发生网络错误: " + e.getMessage(), null);
            return ResponseEntity.status(502).body(result); // 502 Bad Gateway for upstream failure
        } catch (Exception e) {
            logger.error("处理Steam API响应时发生错误", e);
            LoginCheckResult result = new LoginCheckResult(false, false, false, "解析Steam API响应时发生错误: " + e.getMessage(), null);
            return ResponseEntity.status(500).body(result); // 500 Internal Server Error
        }
    }
}
