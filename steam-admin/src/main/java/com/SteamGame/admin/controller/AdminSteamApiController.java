package com.SteamGame.admin.controller;

import com.SteamGame.api.dto.SteamApiSupportedListDTO;
import com.SteamGame.api.service.SteamApiCatalogService;
import com.SteamGame.common.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/steam-api")
public class AdminSteamApiController {

    private final SteamApiCatalogService catalogService;

    public AdminSteamApiController(SteamApiCatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/supported")
    public ApiResponse<SteamApiSupportedListDTO> getSupported(
            @RequestParam(defaultValue = "false") boolean withKey) {
        return ApiResponse.ok(catalogService.getSupportedApiList(withKey));
    }
}
