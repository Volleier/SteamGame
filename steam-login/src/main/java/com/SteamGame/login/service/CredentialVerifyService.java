package com.SteamGame.login.service;

import com.SteamGame.login.dto.CredentialDTO;
import com.SteamGame.login.dto.CredentialCheckResult;
import org.springframework.http.ResponseEntity;

public interface CredentialVerifyService {
    /**
     * 从YAML配置文件读取凭据信息
     */
    CredentialDTO readCredentialFromYaml();

    /**
     * 将 steamId 等可展示信息发送到前端（不包含明文 apiKey）
     */
    ResponseEntity<CredentialDTO> sendCredentialInfoToFrontend();

    /**
     * 验证 YAML 配置中的 steamId 和 apiKey 是否有效
     */
    ResponseEntity<CredentialCheckResult> validateCredential();
}
