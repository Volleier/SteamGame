package com.SteamGame.api.controller;

import com.SteamGame.api.dto.HealthDTO;
import com.SteamGame.common.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@RestController
public class HealthController {

    @GetMapping("/api/health")
    public ApiResponse<HealthDTO> health() {
        HealthDTO dto = new HealthDTO();
        dto.setStatus("UP");
        dto.setApplication("steam-game");
        dto.setTime(ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        return ApiResponse.ok(dto);
    }
}
