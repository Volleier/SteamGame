package com.steam.steamapi.pogo;

import lombok.Data;

@Data
public class GameList {
    private  Integer id;
    private  Integer GameAppid;
    private  String GameName;
    private  Integer GamePlayTime;
}
