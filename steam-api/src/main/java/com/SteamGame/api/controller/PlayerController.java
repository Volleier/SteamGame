package com.SteamGame.api.controller;

import com.SteamGame.api.dto.player.*;
import com.SteamGame.api.service.*;
import com.SteamGame.common.error.BusinessException;
import com.SteamGame.common.error.ErrorCode;
import com.SteamGame.common.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/player")
public class PlayerController {

    private final PlayerProfileService playerProfileService;
    private final RecentGameService recentGameService;
    private final PlayerFriendService friendService;
    private final PlayerWishlistService wishlistService;

    public PlayerController(PlayerProfileService playerProfileService,
                             RecentGameService recentGameService,
                             PlayerFriendService friendService,
                             PlayerWishlistService wishlistService) {
        this.playerProfileService = playerProfileService;
        this.recentGameService = recentGameService;
        this.friendService = friendService;
        this.wishlistService = wishlistService;
    }

    @GetMapping("/profile")
    public ApiResponse<PlayerProfileDTO> getProfile(@RequestParam(defaultValue = "default") String userId) {
        PlayerProfileDTO dto = playerProfileService.getProfile(userId);
        if (dto == null) {
            throw new BusinessException(ErrorCode.STEAM_CREDENTIAL_NOT_FOUND, "Player profile unavailable");
        }
        return ApiResponse.ok(dto);
    }

    @GetMapping("/summary")
    public ApiResponse<PlayerSummaryDTO> getSummary(@RequestParam(defaultValue = "default") String userId) {
        return ApiResponse.ok(playerProfileService.getSummary(userId));
    }

    @GetMapping("/recent-games")
    public ApiResponse<RecentGameResultDTO> getRecentGames(
            @RequestParam(defaultValue = "default") String userId,
            @RequestParam(defaultValue = "10") int count) {
        return ApiResponse.ok(recentGameService.getRecentGames(userId, Math.min(count, 50)));
    }

    @GetMapping("/friends")
    public ApiResponse<PlayerFriendResultDTO> getFriends(@RequestParam(defaultValue = "default") String userId) {
        return ApiResponse.ok(friendService.getFriends(userId));
    }

    @GetMapping("/wishlist")
    public ApiResponse<WishlistResultDTO> getWishlist(@RequestParam(defaultValue = "default") String userId) {
        return ApiResponse.ok(wishlistService.getWishlist(userId));
    }
}
