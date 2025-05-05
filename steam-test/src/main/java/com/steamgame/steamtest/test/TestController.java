package com.steamgame.steamtest.test;

import lombok.Data;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@CrossOrigin(origins = "http://localhost:4173")
public class TestController {
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @PostMapping("/test")
    public String receiveSteamData(@RequestBody SteamRequest request) {
        logger.info("收到请求数据：");
        logger.info("Steam ID: {}", request.getSteamId());
        logger.info("API Key: {}", request.getApiKey());

        // 可以在这里添加处理逻辑

        return "数据接收成功";
    }

    @Data
    public static class SteamRequest {
        private String steamId;
        private String apiKey;
    }
}