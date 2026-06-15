package com.SteamGame.api.controller;

import com.SteamGame.api.domain.OwnedGame;
import com.SteamGame.api.domain.OwnedGameSyncResult;
import com.SteamGame.api.service.OwnedGameService;
import com.SteamGame.common.context.CurrentUser;
import com.SteamGame.common.context.CurrentUserProvider;
import com.SteamGame.common.error.BusinessException;
import com.SteamGame.common.error.ErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OwnedGamesController.class)
class OwnedGamesControllerTest {

    @SpringBootApplication
    static class TestApplication {
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OwnedGameService ownedGameService;

    @MockBean
    private CurrentUserProvider currentUserProvider;

    @Test
    void listReturnsDtoFieldsOnly() throws Exception {
        when(currentUserProvider.currentUser()).thenReturn(new CurrentUser("default", "User", false));
        when(ownedGameService.listOwnedGames(eq("default"))).thenReturn(List.of(sampleGame()));

        mockMvc.perform(get("/api/ownedgames/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].appid").value(730))
                .andExpect(jsonPath("$.data[0].name").value("Counter-Strike 2"))
                .andExpect(jsonPath("$.data[0].playtimeForever").value(120))
                .andExpect(jsonPath("$.data[0].developer").value("Valve"))
                .andExpect(jsonPath("$.data[0]", not(hasKey("userId"))))
                .andExpect(jsonPath("$.data[0]", not(hasKey("steamId"))))
                .andExpect(jsonPath("$.data[0]", not(hasKey("createdAt"))));
    }

    @Test
    void countReturnsCountDto() throws Exception {
        when(currentUserProvider.currentUser()).thenReturn(new CurrentUser("default", "User", false));
        when(ownedGameService.countOwnedGames(eq("default"))).thenReturn(3);

        mockMvc.perform(get("/api/ownedgames/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.count").value(3));
    }

    @Test
    void syncReturnsBusinessErrorWhenCredentialMissing() throws Exception {
        when(currentUserProvider.currentUser()).thenReturn(new CurrentUser("default", "User", false));
        when(ownedGameService.syncOwnedGamesWithResult(eq("default")))
                .thenThrow(new BusinessException(ErrorCode.STEAM_CREDENTIAL_NOT_FOUND, "Steam 凭据未配置"));

        mockMvc.perform(post("/api/ownedgames/sync"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.STEAM_CREDENTIAL_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.msg").value("Steam 凭据未配置"));
    }

    @Test
    void syncReturnsDtoList() throws Exception {
        when(currentUserProvider.currentUser()).thenReturn(new CurrentUser("default", "User", false));
        when(ownedGameService.syncOwnedGamesWithResult(eq("default")))
                .thenReturn(new OwnedGameSyncResult(1, 1, List.of(sampleGame()), true));

        mockMvc.perform(post("/api/ownedgames/sync"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].appid").value(730))
                .andExpect(jsonPath("$.data[0]", not(hasKey("userId"))));
    }

    private OwnedGame sampleGame() {
        OwnedGame game = new OwnedGame();
        game.setUserId("default");
        game.setSteamId("76561198000000000");
        game.setAppid(730L);
        game.setName("Counter-Strike 2");
        game.setPlaytimeForever(120);
        game.setDeveloper("Valve");
        game.setPublisher("Valve");
        game.setReleaseDate("2012-08-21");
        game.setTags("FPS,Multiplayer");
        return game;
    }
}
