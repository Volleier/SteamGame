package com.SteamGame.login.service;

import com.SteamGame.common.response.ApiResponse;
import com.SteamGame.login.dto.CredentialCheckResult;
import com.SteamGame.login.dto.CredentialInputDTO;

public interface CredentialConfigService {

    /**
     * 保存凭据信息到YAML配置文件（入参 DTO），返回统一 ApiResponse 以便控制器直接转发。
     */
    ApiResponse<Object> saveCredentialInfo(CredentialInputDTO credentialDTO);

    /**
     * 在线验证通过后保存凭据 + 校验元数据到 YAML 文件。
     */
    ApiResponse<Object> saveCredentialWithValidation(CredentialInputDTO credentialDTO, CredentialCheckResult validationResult);
}
