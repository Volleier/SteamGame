package com.SteamGame.api.controller;

import com.SteamGame.api.dto.player.*;
import com.SteamGame.api.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PlayerController.class)
class PlayerControllerTest {

    @SpringBootApplication
    static class TestApplication {}

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlayerProfileService profileService;
    @MockBean
    private RecentGameService recentGameService;
    @MockBean
    private PlayerFriendService friendService;
    @MockBean
    private PlayerWishlistService wishlistService;

    @Test
    void getProfileReturnsDto() throws Exception {
        PlayerProfileDTO dto = new PlayerProfileDTO();
        dto.setSteamId("76561198000000000");
        dto.setPersonaName("TestPlayer");
        when(profileService.getProfile(anyString())).thenReturn(dto);

        mockMvc.perform(get("/api/player/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.personaName").value("TestPlayer"));
    }

    @Test
    void getRecentGamesReturnsList() throws Exception {
        RecentGameResultDTO result = new RecentGameResultDTO();
        RecentGameDTO game = new RecentGameDTO();
        game.setAppid(730L);
        game.setName("CS2");
        game.setPlaytime2Weeks(120);
        result.setTotalCount(1);
        result.setGames(List.of(game));
        when(recentGameService.getRecentGames(anyString(), anyInt())).thenReturn(result);

        mockMvc.perform(get("/api/player/recent-games"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.games[0].name").value("CS2"));
    }

    @Test
    void getFriendsReturnsList() throws Exception {
        PlayerFriendResultDTO result = new PlayerFriendResultDTO();
        PlayerFriendDTO friend = new PlayerFriendDTO();
        friend.setSteamId("76561198000000001");
        friend.setRelationship("friend");
        result.setItems(List.of(friend));
        when(friendService.getFriends(anyString())).thenReturn(result);

        mockMvc.perform(get("/api/player/friends"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.items[0].relationship").value("friend"));
    }

    @Test
    void getWishlistReturnsList() throws Exception {
        WishlistResultDTO result = new WishlistResultDTO();
        WishlistItemDTO item = new WishlistItemDTO();
        item.setAppid(570L);
        item.setPriority(1);
        result.setItems(List.of(item));
        when(wishlistService.getWishlist(anyString())).thenReturn(result);

        mockMvc.perform(get("/api/player/wishlist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.items[0].appid").value(570));
    }
}
