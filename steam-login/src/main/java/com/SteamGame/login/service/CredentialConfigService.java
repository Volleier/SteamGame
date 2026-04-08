package com.SteamGame.login.service;

import com.SteamGame.login.dto.CredentialDTO;

public interface CredentialConfigService {

    /**
     * 接收凭据信息（占位/可扩展）
     */
    boolean receiveCredentialInfo(CredentialDTO credentialDTO);

    /**
     * 保存凭据信息到YAML配置文件
     */
    boolean saveCredentialInfo(CredentialDTO credentialDTO);

}
