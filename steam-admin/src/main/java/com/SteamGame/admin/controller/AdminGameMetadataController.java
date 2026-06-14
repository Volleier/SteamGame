package com.SteamGame.admin.controller;

import com.SteamGame.api.service.OwnedGameDetailsService;
import com.SteamGame.common.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 管理后台 — 游戏元数据维护接口。
 */
@RestController
@RequestMapping("/api/admin/game-metadata")
public class AdminGameMetadataController {

    @Autowired(required = false)
    private OwnedGameDetailsService detailsService;

    /**
     * 查询元数据缓存状态。
     */
    @GetMapping
    public ApiResponse<Map<String, Object>> metadataStatus() {
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("detailsServiceAvailable", detailsService != null);
        return ApiResponse.ok(status);
    }

    /**
     * 手动触发详情补全。
     */
    @PostMapping("/sync-details")
    public ApiResponse<Map<String, Object>> syncDetails() {
        if (detailsService == null) {
            return ApiResponse.fail(500, "详情补全服务不可用");
        }
        try {
            int updated = detailsService.syncMissingDetails("default", 50);
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("updated", updated);
            return ApiResponse.ok(result);
        } catch (Exception e) {
            return ApiResponse.fail(500, "详情补全失败: " + e.getMessage());
        }
    }
}
