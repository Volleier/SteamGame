package com.SteamGame.api.controller;

import com.SteamGame.api.domain.OwnedGame;
import com.SteamGame.api.mapper.OwnedGameMapper;
import com.SteamGame.api.service.SteamApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ownedgames")
public class OwnedGamesController {

    private static final Logger logger = LoggerFactory.getLogger(OwnedGamesController.class);

    private final SteamApiService steamApiService;
    private final OwnedGameMapper ownedGameMapper;

    public OwnedGamesController(SteamApiService steamApiService, OwnedGameMapper ownedGameMapper) {
        this.steamApiService = steamApiService;
        this.ownedGameMapper = ownedGameMapper;
    }

    @GetMapping("/fetch")
    public List<OwnedGame> fetchAndList(@RequestParam String steamId, @RequestParam String apiKey) {
        try {
            String body = steamApiService.getOwnedGames(steamId, apiKey);
            logger.info("Fetched OwnedGames JSON length: {}", body == null ? 0 : body.length());
        } catch (Exception e) {
            logger.warn("Error fetching owned games: {}", e.getMessage());
        }
        return ownedGameMapper.listAll();
    }

    @GetMapping("/list")
    public List<OwnedGame> listAll() {
        return ownedGameMapper.listAll();
    }
}
