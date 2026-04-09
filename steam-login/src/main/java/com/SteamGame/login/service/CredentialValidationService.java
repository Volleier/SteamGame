package com.SteamGame.login.service;

import com.SteamGame.login.dto.CredentialCheckResult;

public interface CredentialValidationService {
    /**
     * 在线校验给定 steamId 与明文 apiKey，返回校验结果对象（不包裹 ApiResponse，供上层灵活包装）。
     */
    CredentialCheckResult validateOnline(String steamId, String plainApiKey) throws Exception;
}
