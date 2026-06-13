package com.SteamGame.api.service;

import com.SteamGame.api.domain.SteamCredential;

/**
 * 凭证提供服务 —— 从本地存储中获取解密后的 steamId 和 apiKey。
 * <p>
 * 实现应优先读取内存会话凭证，其次读取 auth.yaml 并解密。
 */
public interface CredentialProvider {

    /**
     * 获取当前用户的解密后 Steam 凭证。
     *
     * @param userId 本地用户 ID，为 null 或空时使用 "default"
     * @return 解密后的凭证，未找到时返回 isValid() == false 的空对象
     */
    SteamCredential getCurrentCredential(String userId);
}
