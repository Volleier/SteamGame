package com.steam.steamapi.apiOperate;

import com.steam.steamapi.pogo.GameList;

import java.util.List;

public interface apiOperate {
    List<GameList> getGame(String apiKey, String steamId);
}
