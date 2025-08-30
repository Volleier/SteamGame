package com.steamgame.steamtest.startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import com.steamgame.steamtest.service.SteamService;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Application startup runner.
 *
 * This listener executes after the Spring Boot application is ready and is
 * used to perform simple, synchronous smoke tests against Steam APIs using
 * the credentials stored in an external auth.yaml file. It is intentionally
 * lightweight and logs responses for manual inspection.
 */
public class StartupRunner implements ApplicationListener<ApplicationReadyEvent> {
    private static final Logger logger = LoggerFactory.getLogger(StartupRunner.class);

    private final SteamService steamService;

    public StartupRunner(SteamService steamService) {
        this.steamService = steamService;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        logger.info("应用已准备就绪，开始启动时的 Steam 检查...");

        String authPath = "E:\\Project\\SteamGame\\auth.yaml";
        Map<String, String> auth = readAuthYaml(authPath);
        String steamId = auth.get("steamid");
        String apiKey = auth.get("apikey");

        if (steamId == null) {
            logger.error("启动器：未在 auth.yaml 中找到 steamid，跳过库存请求与本地 POST");
            return;
        }

        logger.info("启动器：读取到 steamid: {}，apikey: {}", steamId, apiKey == null ? "(未找到)" : "(已找到)");

        // 使用 Steam Web API: IPlayerService/GetOwnedGames/v1/ （需要 apiKey）
        if (apiKey == null || apiKey.isEmpty()) {
            logger.warn("启动器：未在 auth.yaml 中找到 apikey，跳过 GetOwnedGames 请求");
        } else {
            String apiUrl = String.format(
                    "https://api.steampowered.com/IPlayerService/GetOwnedGames/v1/?key=%s&steamid=%s&include_appinfo=1&include_played_free_games=1&format=json",
                    escapeUrl(apiKey), escapeUrl(steamId));
            logger.info("向 Steam Web API GetOwnedGames 发送请求: {}", apiUrl);
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest getReq = HttpRequest.newBuilder().uri(URI.create(apiUrl)).GET().build();
                HttpResponse<String> getResp = client.send(getReq, BodyHandlers.ofString());
                logger.info("GetOwnedGames HTTP 状态码: {}", getResp.statusCode());
                String body = getResp.body();
                logger.info("GetOwnedGames 响应（前 8000 字符）:\n{}", body == null ? "(空)" : (body.length() > 8000 ? body.substring(0, 8000) : body));
            } catch (IOException | InterruptedException e) {
                logger.error("启动器：调用 GetOwnedGames 失败: {}", e.getMessage(), e);
            }
        }

        // 直接调用 SteamService 并记录结果
        try {
            if (apiKey != null && !apiKey.isEmpty()) {
                String owned = steamService.getOwnedGames(steamId, apiKey);
                logger.info("StartupRunner: ownedGames 长度 {}", owned == null ? 0 : owned.length());
            }
        } catch (Exception e) {
            logger.error("StartupRunner: 调用 SteamService 失败: {}", e.getMessage(), e);
        }
    }

    private Map<String, String> readAuthYaml(String absolutePath) {
        Map<String, String> result = new HashMap<>();
        try {
            Path path = Paths.get(absolutePath);
            if (!Files.exists(path)) {
                logger.error("启动器：auth.yaml 文件不存在: {}", absolutePath);
                return result;
            }

            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            for (String raw : lines) {
                if (raw == null) continue;
                String line = raw.trim();
                if (line.startsWith("#") || line.isEmpty()) continue;

                if (line.toLowerCase().startsWith("steamid")) {
                    String[] parts = line.split(":", 2);
                    if (parts.length == 2) result.put("steamid", trimQuotes(parts[1].trim()));
                } else if (line.toLowerCase().startsWith("apikey") || line.toLowerCase().startsWith("api_key")) {
                    String[] parts = line.split(":", 2);
                    if (parts.length == 2) result.put("apikey", trimQuotes(parts[1].trim()));
                }
            }
        } catch (IOException e) {
            logger.error("启动器：读取 auth.yaml 出错: {}", e.getMessage(), e);
        }
        return result;
    }

    private String trimQuotes(String s) {
        if (s == null) return null;
        s = s.trim();
        if ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'"))) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }

    private String escapeUrl(String s) {
        if (s == null) return "";
        try {
            return java.net.URLEncoder.encode(s, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.warn("escapeUrl 编码失败，返回原始字符串: {}", e.getMessage());
            return s;
        }
    }
}
