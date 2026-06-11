package com.SteamGame.login.service;

import com.SteamGame.login.dto.ApiResponse;
import com.SteamGame.login.dto.CredentialViewDTO;
import com.SteamGame.login.dto.CredentialCheckResult;

public interface CredentialVerifyService {
    /**
     * 从YAML配置文件读取凭据信息（用于服务内部），返回展示 DTO（不包含明文 apiKey）
     */
    CredentialViewDTO readCredentialFromYaml();

    /**
     * 将 steamId 等可展示信息发送到前端（不包含明文 apiKey），返回统一 ApiResponse
     */
    ApiResponse<CredentialViewDTO> sendCredentialInfoToFrontend();

    /**
     * 验证 YAML 配置中的 steamId 和 apiKey 是否有效，返回统一 ApiResponse 包含 CredentialCheckResult
     */
    ApiResponse<CredentialCheckResult> validateCredential();
}
