package com.SteamGame.api.service;

import com.SteamGame.api.dto.player.PlayerFriendResultDTO;

public interface PlayerFriendService {
    PlayerFriendResultDTO getFriends(String userId);
}
