package com.SteamGame.api.controller;

import com.SteamGame.api.dto.GameListPageDTO;
import com.SteamGame.api.dto.GameListQuery;
import com.SteamGame.api.service.GameQueryService;
import com.SteamGame.common.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class GamesController {

    private final GameQueryService queryService;

    public GamesController(GameQueryService queryService) { this.queryService = queryService; }

    @GetMapping("/games")
    public ApiResponse<GameListPageDTO> getGames(
            @RequestParam(defaultValue = "default") String userId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        GameListQuery query = new GameListQuery();
        query.setUserId(userId);
        query.setKeyword(keyword);
        query.setGenre(genre);
        query.setCategory(category);
        query.setSort(sort);
        query.setOrder(order);
        query.setPage(page);
        query.setPageSize(Math.min(pageSize, 100));
        return ApiResponse.ok(queryService.queryGames(query));
    }
}
