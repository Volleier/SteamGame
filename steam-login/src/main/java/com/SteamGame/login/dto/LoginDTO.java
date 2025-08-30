package com.SteamGame.login.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginDTO {
    @JsonIgnore
    private String time;

    private String steamId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String apiKey;
}