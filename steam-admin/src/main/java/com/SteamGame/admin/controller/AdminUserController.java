package com.SteamGame.admin.controller;

import com.SteamGame.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 管理后台 — 用户管理接口。
 */
@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    /**
     * MVP: 返回单用户 default 占位数据。
     */
    @GetMapping
    public ApiResponse<Map<String, Object>> listUsers() {
        Map<String, Object> user = new LinkedHashMap<>();
        user.put("userId", "default");
        user.put("username", "User");
        user.put("admin", false);
        user.put("steamIdBound", true);
        return ApiResponse.ok(user);
    }
}
