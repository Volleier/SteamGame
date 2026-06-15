package com.SteamGame.admin.controller;

import com.SteamGame.api.domain.OwnedGameSyncResult;
import com.SteamGame.api.service.OwnedGameService;
import com.SteamGame.common.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 管理后台 — 同步任务管理接口。
 */
@RestController
@RequestMapping("/api/admin/sync-jobs")
public class AdminSyncJobController {

    @Autowired(required = false)
    private OwnedGameService ownedGameService;

    /**
     * 查询最近同步结果概览。
     */
    @GetMapping
    public ApiResponse<Map<String, Object>> syncStatus(@RequestParam(defaultValue = "default") String userId) {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("serviceAvailable", ownedGameService != null);
        if (ownedGameService != null) {
            try {
                int count = ownedGameService.countOwnedGames(userId);
                info.put("ownedGameCount", count);
            } catch (Exception e) {
                info.put("error", e.getMessage());
            }
        }
        return ApiResponse.ok(info);
    }

    /**
     * 手动触发 default 用户同步。
     */
    @PostMapping("/trigger")
    public ApiResponse<Map<String, Object>> triggerSync(@RequestParam(defaultValue = "default") String userId) {
        if (ownedGameService == null) {
            return ApiResponse.fail(500, "同步服务不可用");
        }
        try {
            OwnedGameSyncResult result = ownedGameService.syncOwnedGamesWithResult(userId);
            Map<String, Object> info = new LinkedHashMap<>();
            info.put("total", result.getTotal());
            info.put("saved", result.getSaved());
            info.put("gameCount", result.getGames().size());
            info.put("detailsInProgress", result.isDetailsInProgress());
            return ApiResponse.ok(info);
        } catch (Exception e) {
            return ApiResponse.fail(500, "同步失败: " + e.getMessage());
        }
    }
}
