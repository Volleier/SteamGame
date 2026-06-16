package com.SteamGame.api.domain.player;

import java.sql.Timestamp;

/**
 * Domain entity for player_friend table.
 * Caches Steam friend list from ISteamUser/GetFriendList.
 */
public class PlayerFriend {
    private Long id;
    private String userId;
    private String friendSteamId;
    private String relationship;
    private Long friendSince;
    private Timestamp syncedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getFriendSteamId() { return friendSteamId; }
    public void setFriendSteamId(String friendSteamId) { this.friendSteamId = friendSteamId; }

    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) { this.relationship = relationship; }

    public Long getFriendSince() { return friendSince; }
    public void setFriendSince(Long friendSince) { this.friendSince = friendSince; }

    public Timestamp getSyncedAt() { return syncedAt; }
    public void setSyncedAt(Timestamp syncedAt) { this.syncedAt = syncedAt; }
}
