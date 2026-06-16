package com.SteamGame.api.controller;

import com.SteamGame.api.dto.news.GameNewsResultDTO;
import com.SteamGame.api.service.GameNewsService;
import com.SteamGame.common.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class GameNewsController {

    private final GameNewsService newsService;

    public GameNewsController(GameNewsService newsService) { this.newsService = newsService; }

    @GetMapping("/game-news")
    public ApiResponse<GameNewsResultDTO> getNews(
            @RequestParam Long appid,
            @RequestParam(defaultValue = "10") int count,
            @RequestParam(required = false) Integer maxlength) {
        return ApiResponse.ok(newsService.getNews(appid, Math.min(count, 50), maxlength));
    }
}
