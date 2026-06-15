package com.SteamGame.api.client.steam;

import com.SteamGame.api.client.steam.impl.SteamWebApiClientImpl;
import com.SteamGame.api.config.SteamHttpClientConfig;
import com.SteamGame.api.domain.OwnedGame;
import com.SteamGame.api.dto.player.WishlistItemDTO;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SteamWebApiClientImplTest {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final SteamHttpClientConfig config = new SteamHttpClientConfig();
    private final SteamWebApiClientImpl client = new SteamWebApiClientImpl(httpClient, config);

    @Test
    void parseOwnedGamesExtractsAppidNamePlaytime() throws Exception {
        String json = """
            {
              "response": {
                "games": [
                  { "appid": 730, "name": "Counter-Strike 2", "playtime_forever": 500 },
                  { "appid": 440, "name": "Team Fortress 2", "playtime_forever": 1200 }
                ]
              }
            }
            """;
        List<OwnedGame> games = client.parseOwnedGames(json);
        assertEquals(2, games.size());
        assertEquals(730L, games.get(0).getAppid());
        assertEquals("Counter-Strike 2", games.get(0).getName());
        assertEquals(500, games.get(0).getPlaytimeForever());
    }

    @Test
    void parseOwnedGamesExtractsExtendedFields() throws Exception {
        String json = """
            {
              "response": {
                "games": [
                  {
                    "appid": 730, "name": "Game", "playtime_forever": 100,
                    "playtime_2weeks": 30, "rtime_last_played": 1718420000,
                    "img_icon_url": "hash123", "has_community_visible_stats": true,
                    "playtime_windows_forever": 80, "playtime_mac_forever": 10,
                    "playtime_linux_forever": 5, "playtime_deck_forever": 5
                  }
                ]
              }
            }
            """;
        List<OwnedGame> games = client.parseOwnedGames(json);
        assertEquals(1, games.size());
        OwnedGame g = games.get(0);
        assertEquals(30, g.getPlaytime2weeks());
        assertEquals(1718420000L, g.getRtimeLastPlayed());
        assertEquals("hash123", g.getImgIconUrl());
        assertTrue(g.getHasCommunityVisibleStats());
        assertEquals(80, g.getPlaytimeWindowsForever());
        assertEquals(10, g.getPlaytimeMacForever());
        assertEquals(5, g.getPlaytimeLinuxForever());
        assertEquals(5, g.getPlaytimeDeckForever());
    }

    @Test
    void parseOwnedGamesDoesNotSetUserIdOrSteamId() throws Exception {
        String json = """
            { "response": { "games": [ { "appid": 10, "name": "Game", "playtime_forever": 60 } ] } }
            """;
        List<OwnedGame> games = client.parseOwnedGames(json);
        assertEquals(1, games.size());
        assertNull(games.get(0).getUserId());
        assertNull(games.get(0).getSteamId());
    }

    @Test
    void parseOwnedGamesEmptyResponse() throws Exception {
        String json = "{ \"response\": {} }";
        List<OwnedGame> games = client.parseOwnedGames(json);
        assertTrue(games.isEmpty());
    }

    @Test
    void parseOwnedGamesHandlesMissingOptionalFields() throws Exception {
        String json = """
            { "response": { "games": [ { "appid": 730, "name": "Game", "playtime_forever": 100 } ] } }
            """;
        List<OwnedGame> games = client.parseOwnedGames(json);
        assertEquals(1, games.size());
        OwnedGame g = games.get(0);
        assertNull(g.getPlaytime2weeks());
        assertNull(g.getRtimeLastPlayed());
        assertNull(g.getImgIconUrl());
        assertNull(g.getHasCommunityVisibleStats());
    }

    @Test
    void parseWishlistExtractsItems() throws Exception {
        String json = """
            {
              "response": {
                "items": [
                  { "appid": 570, "priority": 1, "date_added": 1718420000 },
                  { "appid": 730, "name": "Counter-Strike 2", "priority": 2, "added_at": 1718420100 }
                ]
              }
            }
            """;
        List<WishlistItemDTO> items = client.parseWishlist(json);
        assertEquals(2, items.size());
        assertEquals(570L, items.get(0).getAppid());
        assertEquals(1, items.get(0).getPriority());
        assertEquals(1718420000L, items.get(0).getAddedAt());
        assertEquals("Counter-Strike 2", items.get(1).getName());
    }
}
