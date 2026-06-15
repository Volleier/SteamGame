package com.SteamGame.api.client.steam;

import com.SteamGame.api.domain.OwnedGame;
import com.SteamGame.api.dto.player.PlayerProfileDTO;
import com.SteamGame.api.dto.player.RecentGameDTO;
import com.SteamGame.api.dto.player.PlayerFriendDTO;
import com.SteamGame.api.dto.stats.CurrentPlayerCountDTO;
import com.SteamGame.api.dto.achievement.AchievementGlobalPercentDTO;
import com.SteamGame.api.dto.news.GameNewsResultDTO;
import com.SteamGame.api.dto.SteamApiSupportedListDTO;

import java.io.IOException;
import java.util.List;

/**
 * Steam Web API client — calls api.steampowered.com endpoints.
 * Responsible for HTTP requests and JSON parsing only, NOT database operations.
 */
public interface SteamWebApiClient {

    /**
     * ISteamUser/GetPlayerSummaries/v2
     */
    PlayerProfileDTO getPlayerSummary(String steamId, String apiKey) throws IOException, InterruptedException;

    /**
     * IPlayerService/GetOwnedGames/v1
     */
    List<OwnedGame> getOwnedGames(String steamId, String apiKey) throws IOException, InterruptedException;

    /**
     * IPlayerService/GetRecentlyPlayedGames/v1
     */
    List<RecentGameDTO> getRecentlyPlayedGames(String steamId, String apiKey, int count) throws IOException, InterruptedException;

    /**
     * ISteamUserStats/GetNumberOfCurrentPlayers/v1
     */
    CurrentPlayerCountDTO getCurrentPlayers(long appid) throws IOException, InterruptedException;

    /**
     * ISteamUserStats/GetGlobalAchievementPercentagesForApp/v2
     */
    List<AchievementGlobalPercentDTO> getGlobalAchievementPercentages(long appid) throws IOException, InterruptedException;

    /**
     * ISteamNews/GetNewsForApp/v2
     */
    GameNewsResultDTO getNewsForApp(long appid, int count, Integer maxLength) throws IOException, InterruptedException;

    /**
     * ISteamUser/GetFriendList/v1
     */
    List<PlayerFriendDTO> getFriendList(String steamId, String apiKey) throws IOException, InterruptedException;

    /**
     * ISteamWebAPIUtil/GetSupportedAPIList/v1
     */
    SteamApiSupportedListDTO getSupportedApiList(String apiKeyOrNull) throws IOException, InterruptedException;
}
