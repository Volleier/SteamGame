package com.SteamGame.api.controller;

import com.SteamGame.api.dto.metadata.GameMetadataDTO;
import com.SteamGame.api.dto.metadata.GameMetadataSyncResultDTO;
import com.SteamGame.api.service.GameMetadataService;
import com.SteamGame.common.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/game-metadata")
public class GameMetadataController {

    private final GameMetadataService metadataService;

    public GameMetadataController(GameMetadataService metadataService) {
        this.metadataService = metadataService;
    }

    /**
     * GET /api/game-metadata/{appid} — get game metadata.
     */
    @GetMapping("/{appid}")
    public ApiResponse<GameMetadataDTO> getMetadata(@PathVariable Long appid) {
        GameMetadataDTO dto = metadataService.getMetadata(appid);
        return ApiResponse.ok(dto);
    }

    /**
     * POST /api/game-metadata/{appid}/sync — force-sync single game metadata.
     */
    @PostMapping("/{appid}/sync")
    public ApiResponse<GameMetadataDTO> syncMetadata(@PathVariable Long appid) {
        GameMetadataDTO dto = metadataService.syncMetadata(appid, null, null);
        return ApiResponse.ok(dto);
    }

    /**
     * POST /api/game-metadata/sync-missing — batch sync missing metadata.
     */
    @PostMapping("/sync-missing")
    public ApiResponse<GameMetadataSyncResultDTO> syncMissing(@RequestBody Map<String, Object> body) {
        String userId = body.getOrDefault("userId", "default").toString();
        int limit = body.get("limit") instanceof Number n ? n.intValue() : 50;
        GameMetadataSyncResultDTO result = metadataService.syncMissing(userId, Math.min(limit, 100));
        return ApiResponse.ok(result);
    }
}
