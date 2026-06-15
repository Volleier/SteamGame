package com.SteamGame.common.context;

/**
 * 当前用户提供者接口 —— 由各模块实现。
 * MVP 阶段默认实现返回 "default" 用户。
 */
public interface CurrentUserProvider {
    CurrentUser currentUser();
}
