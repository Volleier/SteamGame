package com.SteamGame.api.client;

import com.SteamGame.api.domain.OwnedGame;

import java.io.IOException;
import java.util.List;

/**
 * Steam Web API 客户端 —— 只负责网络请求和响应解析，不负责落库。
 */
public interface SteamApiClient {

    /**
     * 调用 Steam IPlayerService/GetOwnedGames/v1 获取玩家游戏列表。
     *
     * @param steamId 64 位 Steam 用户 ID
     * @param apiKey  Steam Web API Key
     * @return 解析后的游戏列表
     * @throws IOException          网络异常
     * @throws InterruptedException 线程中断
     */
    List<OwnedGame> fetchOwnedGames(String steamId, String apiKey) throws IOException, InterruptedException;
}
