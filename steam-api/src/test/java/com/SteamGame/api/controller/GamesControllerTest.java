package com.SteamGame.api.controller;

import com.SteamGame.api.dto.GameListItemDTO;
import com.SteamGame.api.dto.GameListPageDTO;
import com.SteamGame.api.dto.GameListQuery;
import com.SteamGame.api.service.GameQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GamesController.class)
class GamesControllerTest {

    @SpringBootApplication
    static class TestApplication {}

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameQueryService queryService;

    @Test
    void getGamesReturnsPagedResult() throws Exception {
        GameListPageDTO page = new GameListPageDTO();
        GameListItemDTO item = new GameListItemDTO();
        item.setAppid(730L);
        item.setName("Counter-Strike 2");
        item.setPlaytimeForever(500);
        page.setItems(List.of(item));
        page.setPage(1);
        page.setPageSize(20);
        page.setTotal(1);
        when(queryService.queryGames(any(GameListQuery.class))).thenReturn(page);

        mockMvc.perform(get("/api/games"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.items[0].appid").value(730))
                .andExpect(jsonPath("$.data.items[0].name").value("Counter-Strike 2"))
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    void getGamesSupportsSearchAndFilter() throws Exception {
        GameListPageDTO page = new GameListPageDTO();
        page.setItems(List.of());
        page.setTotal(0);
        when(queryService.queryGames(any(GameListQuery.class))).thenReturn(page);

        mockMvc.perform(get("/api/games?keyword=test&genre=Action&sort=playtime&order=desc&page=1&pageSize=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
