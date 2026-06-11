package com.SteamGame.login.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存凭据存储 — rememberMe=false 时使用。
 * 应用停止后自动失效。
 */
@Component
public class CredentialSessionStore {

    private static final Logger logger = LoggerFactory.getLogger(CredentialSessionStore.class);

    public static class SessionEntry {
        public final String steamId;
        public final String encryptedApiKey;
        public final String iv;
        public boolean authenticated;
        public boolean validKeyAndUser;
        public boolean ownsGames;
        public boolean profilePrivate;
        public int gameCount;
        public long validatedAtEpochMs;

        SessionEntry(String steamId, String encryptedApiKey, String iv) {
            this.steamId = steamId;
            this.encryptedApiKey = encryptedApiKey;
            this.iv = iv;
            this.authenticated = true;
        }
    }

    // userId → entry; single-user app => "default"
    private final Map<String, SessionEntry> sessions = new ConcurrentHashMap<>();

    public void save(String userId, String steamId, String encryptedApiKey, String iv) {
        SessionEntry entry = new SessionEntry(steamId, encryptedApiKey, iv);
        sessions.put(userIdOrDefault(userId), entry);
        logger.info("内存凭据已保存 — steamId={}, persisted=false", steamId);
    }

    public Optional<SessionEntry> get(String userId) {
        return Optional.ofNullable(sessions.get(userIdOrDefault(userId)));
    }

    public boolean hasCredential(String userId) {
        return sessions.containsKey(userIdOrDefault(userId));
    }

    public void markAuthenticated(String userId, boolean authenticated) {
        SessionEntry entry = sessions.get(userIdOrDefault(userId));
        if (entry != null) {
            entry.authenticated = authenticated;
        }
    }

    public void setValidationResult(String userId, boolean validKeyAndUser, boolean ownsGames,
                                     boolean profilePrivate, int gameCount) {
        SessionEntry entry = sessions.get(userIdOrDefault(userId));
        if (entry != null) {
            entry.validKeyAndUser = validKeyAndUser;
            entry.ownsGames = ownsGames;
            entry.profilePrivate = profilePrivate;
            entry.gameCount = gameCount;
            entry.validatedAtEpochMs = Instant.now().toEpochMilli();
        }
    }

    public void clear(String userId) {
        sessions.remove(userIdOrDefault(userId));
        logger.info("内存凭据已清除 — userId={}", userIdOrDefault(userId));
    }

    private String userIdOrDefault(String userId) {
        return userId != null && !userId.isEmpty() ? userId : "default";
    }
}
