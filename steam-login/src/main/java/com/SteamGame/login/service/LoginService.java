package com.SteamGame.login.service;

import com.SteamGame.login.dto.LoginDTO;

public interface LoginService {
/**
     * 保存登录信息到YAML配置文件
     *
     * @param loginDTO 登录信息(time, steam_id, api_key, rememberMe)
     * @return 操作是否成功
     */
    boolean saveLoginInfo(LoginDTO loginDTO);
}
