package com.SteamGame.api.dto.player;

/**
 * DTO for a single friend entry.
 */
public class PlayerFriendDTO {
    private String steamId;
    private String relationship;
    private Long friendSince;

    public String getSteamId() { return steamId; }
    public void setSteamId(String steamId) { this.steamId = steamId; }
    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) { this.relationship = relationship; }
    public Long getFriendSince() { return friendSince; }
    public void setFriendSince(Long friendSince) { this.friendSince = friendSince; }
}
