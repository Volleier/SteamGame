package com.SteamGame.login.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "login.cache")
public class CredentialProperties {
    /**
     * 默认重校验间隔（小时）
     */
    private long revalidateHours = 6;

    /**
     * 校验失败后的快速重试间隔（小时）
     */
    private long failRetryHours = 1;

    public long getRevalidateHours() {
        return revalidateHours;
    }

    public void setRevalidateHours(long revalidateHours) {
        this.revalidateHours = revalidateHours;
    }

    public long getFailRetryHours() {
        return failRetryHours;
    }

    public void setFailRetryHours(long failRetryHours) {
        this.failRetryHours = failRetryHours;
    }
}
