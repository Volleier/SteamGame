package com.SteamGame.login.service;

import com.SteamGame.login.dto.ApiResponse;
import com.SteamGame.login.dto.CredentialInputDTO;
import com.SteamGame.login.dto.CredentialCheckResult;

public interface CredentialService {

    /**
     * 注册/保存凭据并立即执行在线校验（编排上层调用）。
     */
    ApiResponse<Object> registerAndValidate(CredentialInputDTO dto);

    /**
     * 登录时从配置加载并根据缓存策略决定是否触发在线重校验。
     */
    ApiResponse<CredentialCheckResult> loadAndValidateForLogin(String userId);
}
