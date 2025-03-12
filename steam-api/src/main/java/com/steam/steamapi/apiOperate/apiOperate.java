package com.steam.steamapi.apiOperate;

import com.steam.steamapi.pogo.GameList;

import java.util.List;

public interface apiOperate {
    List<GameList> getGameList(String apiKey, String steamId);
}