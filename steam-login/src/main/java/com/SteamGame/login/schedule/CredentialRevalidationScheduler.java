package com.SteamGame.login.schedule;

import com.SteamGame.login.model.CredentialRecord;
import com.SteamGame.login.repository.CredentialRepository;
import com.SteamGame.login.service.CredentialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.SteamGame.login.config.CredentialProperties;

import java.util.List;

@Component
public class CredentialRevalidationScheduler {

    private static final Logger logger = LoggerFactory.getLogger(CredentialRevalidationScheduler.class);

    @Autowired(required = false)
    private CredentialRepository credentialRepository;

    @Autowired(required = false)
    private CredentialService credentialService;

    @Autowired(required = false)
    private CredentialProperties credentialProperties;

    private final java.util.concurrent.atomic.AtomicBoolean running = new java.util.concurrent.atomic.AtomicBoolean(
            false);

    // 默认每 10 分钟运行一次，支持通过 login.scheduler.fixedDelayMs 覆盖（毫秒）
    @Scheduled(fixedDelayString = "${login.scheduler.fixedDelayMs:600000}")
    public void runRevalidation() {
        if (!running.compareAndSet(false, true)) {
            logger.info("上一次重校验任务仍在运行，跳过本次执行");
            return;
        }

        try {
            if (credentialRepository == null || credentialService == null) {
                logger.debug("CredentialRevalidationScheduler 未启用（缺少 repository 或 service 注入）");
                return;
            }

            if (credentialProperties != null && !credentialProperties.isSchedulerEnabled()) {
                logger.debug("调度器被配置为禁用，跳过运行");
                return;
            }

            long now = System.currentTimeMillis();
            List<CredentialRecord> due = credentialRepository.findDueForRevalidation(now);
            if (due == null || due.isEmpty()) {
                logger.debug("无到期待重校验的凭据");
                return;
            }

            int maxPerRun = credentialProperties != null ? credentialProperties.getMaxPerRun() : 50;
            logger.info("发现 {} 个凭据到期，准备处理最多 {} 个", due.size(), maxPerRun);

            int processed = 0;
            java.time.format.DateTimeFormatter tf = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (CredentialRecord r : due) {
                if (processed >= maxPerRun)
                    break;
                try {
                    // 根据失败次数与最后校验时间决定是否跳过（短期重试保护）
                    boolean skip = false;
                    if (r.getValidation() != null && r.getValidation().getFailCount() > 0
                            && r.getValidation().getLastValidatedAt() != null) {
                        try {
                            java.time.LocalDateTime last = java.time.LocalDateTime
                                    .parse(r.getValidation().getLastValidatedAt(), tf);
                            long hoursSince = java.time.Duration
                                    .between(last, java.time.LocalDateTime.now(java.time.ZoneOffset.UTC)).toHours();
                            long failRetryHours = credentialProperties != null
                                    ? credentialProperties.getFailRetryHours()
                                    : 1;
                            if (hoursSince < failRetryHours) {
                                skip = true;
                            }
                        } catch (Exception ex) {
                            // parse error -> don't skip
                        }
                    }

                    if (skip) {
                        logger.debug("跳过 userId={} 的短期重试（failCount={}）", r.getUserId(),
                                r.getValidation() != null ? r.getValidation().getFailCount() : 0);
                        continue;
                    }

                    String userId = r.getUserId();
                    logger.info("对 userId={} 执行重校验", userId);
                    try {
                        credentialService.loadAndValidateForLogin(userId);
                    } catch (Exception ex) {
                        logger.warn("重校验 userId={} 时发生异常: {}", userId, ex.getMessage());
                    }
                    processed++;
                } catch (Exception e) {
                    logger.error("处理重校验记录时发生错误", e);
                }
            }
            logger.info("本次重校验完成，实际处理 {} 条记录", processed);
        } finally {
            running.set(false);
        }
    }
}
