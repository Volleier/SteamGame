package com.SteamGame.admin.controller;

import com.SteamGame.common.response.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 管理后台 — 系统配置查询接口（初期只读）。
 */
@RestController
@RequestMapping("/api/admin/system-config")
public class AdminSystemConfigController {

    @Value("${steam.api.timeoutSeconds:15}")
    private int steamApiTimeoutSeconds;

    @Value("${steam.api.detailsTimeoutSeconds:6}")
    private int detailsTimeoutSeconds;

    @Value("${steam.api.detailsDelayMillis:1500}")
    private int detailsDelayMillis;

    @Value("${login.config.path:auth.yaml}")
    private String configPath;

    @Value("${login.cache.revalidateHours:6}")
    private int revalidateHours;

    @Value("${steam.api.maxBatchSize:50}")
    private int maxBatchSize;

    @Value("${steam.api.storeLanguage:zh-cn}")
    private String storeLanguage;

    @Value("${steam.api.storeCountryCode:CN}")
    private String storeCountryCode;

    /**
     * 查询当前系统配置（只读）。
     */
    @GetMapping
    public ApiResponse<Map<String, Object>> config() {
        Map<String, Object> cfg = new LinkedHashMap<>();
        cfg.put("steamApiTimeoutSeconds", steamApiTimeoutSeconds);
        cfg.put("detailsTimeoutSeconds", detailsTimeoutSeconds);
        cfg.put("detailsDelayMillis", detailsDelayMillis);
        cfg.put("maxBatchSize", maxBatchSize);
        cfg.put("storeLanguage", storeLanguage);
        cfg.put("storeCountryCode", storeCountryCode);
        cfg.put("configPath", configPath);
        cfg.put("credentialRevalidateHours", revalidateHours);
        return ApiResponse.ok(cfg);
    }
}
