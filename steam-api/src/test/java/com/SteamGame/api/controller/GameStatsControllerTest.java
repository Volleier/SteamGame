package com.SteamGame.api.controller;

import com.SteamGame.api.dto.stats.CurrentPlayerCountDTO;
import com.SteamGame.api.service.GameStatsService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GameStatsController.class)
class GameStatsControllerTest {

    @SpringBootApplication
    static class TestApplication {}

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameStatsService statsService;

    @Test
    void getCurrentPlayersReturnsDto() throws Exception {
        CurrentPlayerCountDTO dto = new CurrentPlayerCountDTO();
        dto.setAppid(730L);
        dto.setPlayerCount(500000);
        dto.setCached(true);
        when(statsService.getCurrentPlayers(anyLong(), anyBoolean())).thenReturn(dto);

        mockMvc.perform(get("/api/game-stats/current-players?appid=730"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.appid").value(730))
                .andExpect(jsonPath("$.data.playerCount").value(500000));
    }

    @Test
    void batchReturnsResults() throws Exception {
        CurrentPlayerCountDTO dto = new CurrentPlayerCountDTO();
        dto.setAppid(730L);
        dto.setPlayerCount(500000);
        when(statsService.getCurrentPlayersBatch(anyList(), anyBoolean())).thenReturn(List.of(dto));

        mockMvc.perform(post("/api/game-stats/current-players/batch")
                        .contentType("application/json")
                        .content("{\"appids\":[730,570],\"forceRefresh\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.items[0].appid").value(730));
    }
}
