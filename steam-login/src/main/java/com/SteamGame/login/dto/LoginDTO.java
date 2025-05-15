package com.SteamGame.login.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginDTO {
    // getters and setters
    private String time;
    private String steamId;
    private String apiKey;
    private boolean rememberMe;
}