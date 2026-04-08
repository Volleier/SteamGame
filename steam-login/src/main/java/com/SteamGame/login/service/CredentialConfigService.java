package com.SteamGame.login.service;

import com.SteamGame.login.dto.ApiResponse;
import com.SteamGame.login.dto.CredentialInputDTO;

public interface CredentialConfigService {

    /**
     * 保存凭据信息到YAML配置文件（入参 DTO），返回统一 ApiResponse 以便控制器直接转发。
     */
    ApiResponse<Object> saveCredentialInfo(CredentialInputDTO credentialDTO);

}
