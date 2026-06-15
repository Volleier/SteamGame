package com.SteamGame.api.controller;

import com.SteamGame.api.dto.player.PlayerProfileDTO;
import com.SteamGame.api.dto.player.PlayerSummaryDTO;
import com.SteamGame.api.service.PlayerProfileService;
import com.SteamGame.common.error.BusinessException;
import com.SteamGame.common.error.ErrorCode;
import com.SteamGame.common.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/player")
public class PlayerController {

    private final PlayerProfileService playerProfileService;

    public PlayerController(PlayerProfileService playerProfileService) {
        this.playerProfileService = playerProfileService;
    }

    /**
     * GET /api/player/profile — Steam player profile.
     */
    @GetMapping("/profile")
    public ApiResponse<PlayerProfileDTO> getProfile(@RequestParam(defaultValue = "default") String userId) {
        PlayerProfileDTO dto = playerProfileService.getProfile(userId);
        if (dto == null) {
            throw new BusinessException(ErrorCode.STEAM_CREDENTIAL_NOT_FOUND, "Player profile unavailable");
        }
        return ApiResponse.ok(dto);
    }

    /**
     * GET /api/player/summary — Dashboard aggregated summary.
     */
    @GetMapping("/summary")
    public ApiResponse<PlayerSummaryDTO> getSummary(@RequestParam(defaultValue = "default") String userId) {
        return ApiResponse.ok(playerProfileService.getSummary(userId));
    }
}
