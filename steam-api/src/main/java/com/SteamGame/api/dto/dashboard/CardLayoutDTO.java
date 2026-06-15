package com.SteamGame.api.dto.dashboard;

import lombok.Data;

@Data
public class CardLayoutDTO {
    private int id;
    private int x;
    private int y;
    private int w;
    private boolean visible;
}
