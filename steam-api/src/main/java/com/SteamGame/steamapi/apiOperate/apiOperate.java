package com.SteamGame.steamapi.apiOperate;

import com.SteamGame.steamapi.pogo.GameList;

import java.util.List;

public interface apiOperate {
    List<GameList> getGameList(String apiKey, String steamId);
}