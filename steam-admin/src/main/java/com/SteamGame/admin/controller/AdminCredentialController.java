package com.SteamGame.admin.controller;

import com.SteamGame.common.response.ApiResponse;
import com.SteamGame.login.service.CredentialVerifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 管理后台 — 凭据状态管理接口。
 */
@RestController
@RequestMapping("/api/admin/credentials")
public class AdminCredentialController {

    @Autowired(required = false)
    private CredentialVerifyService credentialVerifyService;

    /**
     * 查看凭据配置状态（不返回明文 API Key）。
     */
    @GetMapping
    public ApiResponse<Map<String, Object>> credentialStatus() {
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("configured", credentialVerifyService != null);
        if (credentialVerifyService != null) {
            try {
                var view = credentialVerifyService.readCredentialFromYaml();
                status.put("steamId", view.getSteamId());
                status.put("hasApiKey", view.isHasApiKey());
                status.put("updatedAt", view.getUpdatedAt());
            } catch (Exception e) {
                status.put("error", e.getMessage());
            }
        }
        return ApiResponse.ok(status);
    }
}
