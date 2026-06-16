package com.SteamGame.api.controller;

import com.SteamGame.api.dto.metadata.GameMetadataDTO;
import com.SteamGame.api.dto.metadata.GameMetadataSyncResultDTO;
import com.SteamGame.api.service.GameMetadataService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GameMetadataController.class)
class GameMetadataControllerTest {

    @SpringBootApplication
    static class TestApplication {}

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameMetadataService metadataService;

    @Test
    void getMetadataReturnsDto() throws Exception {
        when(metadataService.getMetadata(anyLong())).thenReturn(sampleMetadata());

        mockMvc.perform(get("/api/game-metadata/730"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.appid").value(730))
                .andExpect(jsonPath("$.data.name").value("Test Game"));
    }

    @Test
    void syncMetadataReturnsDto() throws Exception {
        when(metadataService.syncMetadata(anyLong(), any(), any())).thenReturn(sampleMetadata());

        mockMvc.perform(post("/api/game-metadata/730/sync"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.appid").value(730));
    }

    @Test
    void syncMissingReturnsResult() throws Exception {
        GameMetadataSyncResultDTO result = new GameMetadataSyncResultDTO();
        result.setRequested(10);
        result.setUpdated(8);
        result.setFailed(2);
        when(metadataService.syncMissing(anyString(), anyInt())).thenReturn(result);

        mockMvc.perform(post("/api/game-metadata/sync-missing")
                        .contentType("application/json")
                        .content("{\"userId\":\"default\",\"limit\":10}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.requested").value(10))
                .andExpect(jsonPath("$.data.updated").value(8))
                .andExpect(jsonPath("$.data.failed").value(2));
    }

    private GameMetadataDTO sampleMetadata() {
        GameMetadataDTO dto = new GameMetadataDTO();
        dto.setAppid(730L);
        dto.setName("Test Game");
        dto.setType("game");
        return dto;
    }
}
