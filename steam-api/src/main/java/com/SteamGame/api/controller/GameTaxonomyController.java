package com.SteamGame.api.controller;

import com.SteamGame.api.dto.GameTaxonomyDTO;
import com.SteamGame.api.service.GameTaxonomyService;
import com.SteamGame.common.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class GameTaxonomyController {

    private final GameTaxonomyService taxonomyService;

    public GameTaxonomyController(GameTaxonomyService taxonomyService) { this.taxonomyService = taxonomyService; }

    @GetMapping("/game-taxonomy")
    public ApiResponse<GameTaxonomyDTO> getTaxonomy() {
        return ApiResponse.ok(taxonomyService.getTaxonomy());
    }
}
