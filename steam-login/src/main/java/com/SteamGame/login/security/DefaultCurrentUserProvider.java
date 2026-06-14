package com.SteamGame.login.security;

import com.SteamGame.common.context.CurrentUser;
import com.SteamGame.common.context.CurrentUserProvider;
import org.springframework.stereotype.Component;

/**
 * MVP 默认用户提供者 —— 返回固定 "default" 用户。
 * 后续接入真实登录后，在此读取 Token/JWT/session 获取真实用户信息。
 */
@Component
public class DefaultCurrentUserProvider implements CurrentUserProvider {

    @Override
    public CurrentUser currentUser() {
        return new CurrentUser("default", "User", false);
    }
}
