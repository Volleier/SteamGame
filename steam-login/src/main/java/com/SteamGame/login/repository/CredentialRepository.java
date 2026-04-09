package com.SteamGame.login.repository;

import com.SteamGame.login.model.CredentialRecord;
import java.util.List;
import java.util.Optional;

public interface CredentialRepository {
    Optional<CredentialRecord> findByUserId(String userId);

    void upsert(CredentialRecord record);

    List<CredentialRecord> findDueForRevalidation(long nowEpochMillis);
}
