package com.SteamGame.api.dto.player;

/**
 * DTO for Steam player profile (GET /api/player/profile).
 */
public class PlayerProfileDTO {
    private String steamId;
    private String personaName;
    private String profileUrl;
    private String avatar;
    private String avatarMedium;
    private String avatarFull;
    private Integer personaState;
    private Integer communityVisibilityState;
    private Long lastLogoff;
    private Long timeCreated;
    private String countryCode;

    public String getSteamId() { return steamId; }
    public void setSteamId(String steamId) { this.steamId = steamId; }
    public String getPersonaName() { return personaName; }
    public void setPersonaName(String personaName) { this.personaName = personaName; }
    public String getProfileUrl() { return profileUrl; }
    public void setProfileUrl(String profileUrl) { this.profileUrl = profileUrl; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public String getAvatarMedium() { return avatarMedium; }
    public void setAvatarMedium(String avatarMedium) { this.avatarMedium = avatarMedium; }
    public String getAvatarFull() { return avatarFull; }
    public void setAvatarFull(String avatarFull) { this.avatarFull = avatarFull; }
    public Integer getPersonaState() { return personaState; }
    public void setPersonaState(Integer personaState) { this.personaState = personaState; }
    public Integer getCommunityVisibilityState() { return communityVisibilityState; }
    public void setCommunityVisibilityState(Integer communityVisibilityState) { this.communityVisibilityState = communityVisibilityState; }
    public Long getLastLogoff() { return lastLogoff; }
    public void setLastLogoff(Long lastLogoff) { this.lastLogoff = lastLogoff; }
    public Long getTimeCreated() { return timeCreated; }
    public void setTimeCreated(Long timeCreated) { this.timeCreated = timeCreated; }
    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
}
