package com.SteamGame.login.service.impl;

import com.SteamGame.login.config.CredentialProperties;
import com.SteamGame.login.model.CredentialValidationMeta;
import com.SteamGame.login.service.CredentialCachePolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
public class FixedIntervalCredentialCachePolicy implements CredentialCachePolicy {

    private final CredentialProperties props;
    private static final DateTimeFormatter TF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public FixedIntervalCredentialCachePolicy(CredentialProperties props) {
        this.props = props;
    }

    @Override
    public boolean isValidationFresh(CredentialValidationMeta meta) {
        if (meta == null || meta.getNextRevalidateAt() == null)
            return false;
        try {
            LocalDateTime next = LocalDateTime.parse(meta.getNextRevalidateAt(), TF);
            return next.isAfter(LocalDateTime.now(ZoneOffset.UTC));
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public String computeNextRevalidateAt(boolean success) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        LocalDateTime next = success ? now.plusHours(props.getRevalidateHours())
                : now.plusHours(props.getFailRetryHours());
        return next.format(TF);
    }
}
