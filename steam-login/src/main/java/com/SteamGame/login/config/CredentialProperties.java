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

    /**
     * 每次调度最多处理的记录数
     */
    private int maxPerRun = 50;

    /**
     * 调度器重入保护标志（保留，启用 by default）
     */
    private boolean schedulerEnabled = true;

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

    public int getMaxPerRun() {
        return maxPerRun;
    }

    public void setMaxPerRun(int maxPerRun) {
        this.maxPerRun = maxPerRun;
    }

    public boolean isSchedulerEnabled() {
        return schedulerEnabled;
    }

    public void setSchedulerEnabled(boolean schedulerEnabled) {
        this.schedulerEnabled = schedulerEnabled;
    }
}
