package com.SteamGame.login.service;

import com.SteamGame.login.model.CredentialValidationMeta;

public interface CredentialCachePolicy {
    /**
     * 判断已存储的验证元信息是否仍然新鲜（无需在线重校验）。
     */
    boolean isValidationFresh(CredentialValidationMeta meta);

    /**
     * 根据校验成功或失败计算下一次重校验时间（ISO 格式使用 yyyy-MM-dd HH:mm:ss）。
     */
    String computeNextRevalidateAt(boolean success);
}
