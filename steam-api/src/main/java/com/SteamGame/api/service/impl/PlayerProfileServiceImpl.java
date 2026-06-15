package com.SteamGame.api.service.impl;

import com.SteamGame.api.client.steam.SteamWebApiClient;
import com.SteamGame.api.domain.player.PlayerProfile;
import com.SteamGame.api.dto.player.PlayerProfileDTO;
import com.SteamGame.api.dto.player.PlayerSummaryDTO;
import com.SteamGame.api.mapper.OwnedGameMapper;
import com.SteamGame.api.mapper.PlayerProfileMapper;
import com.SteamGame.api.mapper.RecentGameMapper;
import com.SteamGame.api.service.PlayerProfileService;
import com.SteamGame.common.context.CredentialProvider;
import com.SteamGame.common.context.SteamCredential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class PlayerProfileServiceImpl implements PlayerProfileService {

    private static final Logger log = LoggerFactory.getLogger(PlayerProfileServiceImpl.class);

    private final CredentialProvider credentialProvider;
    private final SteamWebApiClient webApiClient;
    private final PlayerProfileMapper profileMapper;
    private final OwnedGameMapper ownedGameMapper;
    private final RecentGameMapper recentGameMapper;

    public PlayerProfileServiceImpl(CredentialProvider credentialProvider,
                                     SteamWebApiClient webApiClient,
                                     PlayerProfileMapper profileMapper,
                                     OwnedGameMapper ownedGameMapper,
                                     RecentGameMapper recentGameMapper) {
        this.credentialProvider = credentialProvider;
        this.webApiClient = webApiClient;
        this.profileMapper = profileMapper;
        this.ownedGameMapper = ownedGameMapper;
        this.recentGameMapper = recentGameMapper;
    }

    @Override
    public PlayerProfileDTO getProfile(String userId) {
        // Fetch from Steam first for live status
        try {
            SteamCredential cred = credentialProvider.getCurrentCredential(userId);
            if (cred != null && cred.isValid()) {
                PlayerProfileDTO dto = webApiClient.getPlayerSummary(cred.getSteamId(), cred.getApiKey());
                if (dto != null) {
                    saveProfile(userId, dto);
                    return dto;
                }
            } else {
                log.warn("No valid credential for userId={}", userId);
            }
        } catch (Exception e) {
            log.error("Failed to fetch player profile for userId={}: {}", userId, e.getMessage());
        }

        // Fallback to local cache
        PlayerProfile cached = profileMapper.findByUserId(userId);
        if (cached != null) {
            return toProfileDTO(cached);
        }

        return null;
    }

    @Override
    public PlayerSummaryDTO getSummary(String userId) {
        PlayerSummaryDTO summary = new PlayerSummaryDTO();

        // Profile
        summary.setProfile(getProfile(userId));

        // Game stats
        int gameCount = ownedGameMapper.countByUserId(userId);
        summary.setOwnedGameCount(gameCount);
        summary.setRecentGameCount(recentGameMapper.countByUserId(userId));
        summary.setTotalPlaytimeForever(ownedGameMapper.sumPlaytimeForeverByUserId(userId));
        summary.setLastSyncedAt(ownedGameMapper.findLastSyncedAtByUserId(userId));

        return summary;
    }

    private void saveProfile(String userId, PlayerProfileDTO dto) {
        PlayerProfile profile = new PlayerProfile();
        profile.setUserId(userId);
        profile.setSteamId(dto.getSteamId());
        profile.setPersonaName(dto.getPersonaName());
        profile.setProfileUrl(dto.getProfileUrl());
        profile.setAvatar(dto.getAvatar());
        profile.setAvatarMedium(dto.getAvatarMedium());
        profile.setAvatarFull(dto.getAvatarFull());
        profile.setPersonaState(dto.getPersonaState());
        profile.setCommunityVisibilityState(dto.getCommunityVisibilityState());
        profile.setLastLogoff(dto.getLastLogoff());
        profile.setTimeCreated(dto.getTimeCreated());
        profile.setCountryCode(dto.getCountryCode());
        Timestamp now = new Timestamp(System.currentTimeMillis());
        profile.setSyncedAt(now);
        profile.setUpdatedAt(now);
        profileMapper.upsert(profile);
    }

    private PlayerProfileDTO toProfileDTO(PlayerProfile p) {
        PlayerProfileDTO dto = new PlayerProfileDTO();
        dto.setSteamId(p.getSteamId());
        dto.setPersonaName(p.getPersonaName());
        dto.setProfileUrl(p.getProfileUrl());
        dto.setAvatar(p.getAvatar());
        dto.setAvatarMedium(p.getAvatarMedium());
        dto.setAvatarFull(p.getAvatarFull());
        dto.setPersonaState(p.getPersonaState());
        dto.setCommunityVisibilityState(p.getCommunityVisibilityState());
        dto.setLastLogoff(p.getLastLogoff());
        dto.setTimeCreated(p.getTimeCreated());
        dto.setCountryCode(p.getCountryCode());
        return dto;
    }
}
