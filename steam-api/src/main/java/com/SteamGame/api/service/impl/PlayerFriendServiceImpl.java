package com.SteamGame.api.service.impl;

import com.SteamGame.api.client.steam.SteamWebApiClient;
import com.SteamGame.api.domain.player.PlayerFriend;
import com.SteamGame.api.dto.player.PlayerFriendDTO;
import com.SteamGame.api.dto.player.PlayerFriendResultDTO;
import com.SteamGame.api.mapper.PlayerFriendMapper;
import com.SteamGame.api.service.PlayerFriendService;
import com.SteamGame.common.context.CredentialProvider;
import com.SteamGame.common.context.SteamCredential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class PlayerFriendServiceImpl implements PlayerFriendService {

    private static final Logger log = LoggerFactory.getLogger(PlayerFriendServiceImpl.class);

    private final CredentialProvider credentialProvider;
    private final SteamWebApiClient webApiClient;
    private final PlayerFriendMapper friendMapper;

    public PlayerFriendServiceImpl(CredentialProvider credentialProvider,
                                    SteamWebApiClient webApiClient,
                                    PlayerFriendMapper friendMapper) {
        this.credentialProvider = credentialProvider;
        this.webApiClient = webApiClient;
        this.friendMapper = friendMapper;
    }

    @Override
    public PlayerFriendResultDTO getFriends(String userId) {
        try {
            SteamCredential cred = credentialProvider.getCurrentCredential(userId);
            if (cred != null && cred.isValid()) {
                List<PlayerFriendDTO> dtos = webApiClient.getFriendList(cred.getSteamId(), cred.getApiKey());
                if (dtos != null) {
                    Timestamp now = new Timestamp(System.currentTimeMillis());
                    for (var dto : dtos) {
                        PlayerFriend friend = new PlayerFriend();
                        friend.setUserId(userId);
                        friend.setFriendSteamId(dto.getSteamId());
                        friend.setRelationship(dto.getRelationship());
                        friend.setFriendSince(dto.getFriendSince());
                        friend.setSyncedAt(now);
                        friendMapper.upsert(friend);
                    }
                    PlayerFriendResultDTO result = new PlayerFriendResultDTO();
                    result.setItems(dtos);
                    return result;
                }
            }
        } catch (Exception e) {
            log.warn("Friend list unavailable for userId={}: {} (may be due to privacy settings)", userId, e.getMessage());
        }
        PlayerFriendResultDTO empty = new PlayerFriendResultDTO();
        empty.setItems(List.of());
        return empty;
    }
}
