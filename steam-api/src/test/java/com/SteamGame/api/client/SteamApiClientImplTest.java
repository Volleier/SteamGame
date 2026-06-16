package com.SteamGame.api.client;

import com.SteamGame.api.client.steam.impl.SteamWebApiClientImpl;
import com.SteamGame.api.config.SteamHttpClientConfig;
import com.SteamGame.api.domain.OwnedGame;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Backward-compatible test that now exercises the refactored SteamWebApiClientImpl.
 */
class SteamApiClientImplTest {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final SteamHttpClientConfig config = new SteamHttpClientConfig();
    private final SteamWebApiClientImpl client = new SteamWebApiClientImpl(httpClient, config);

    @Test
    void parseGamesFromJsonExtractsAppidNamePlaytime() throws Exception {
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
        assertEquals(440L, games.get(1).getAppid());
        assertEquals("Team Fortress 2", games.get(1).getName());
        assertEquals(1200, games.get(1).getPlaytimeForever());
    }

    @Test
    void parseGamesFromJsonDoesNotSetUserIdOrSteamId() throws Exception {
        String json = """
            {
              "response": {
                "games": [
                  { "appid": 10, "name": "Game", "playtime_forever": 60 }
                ]
              }
            }
            """;

        List<OwnedGame> games = client.parseOwnedGames(json);

        assertEquals(1, games.size());
        assertNull(games.get(0).getUserId());
        assertNull(games.get(0).getSteamId());
        assertNull(games.get(0).getLastSyncedAt());
    }

    @Test
    void parseGamesFromJsonEmptyResponse() throws Exception {
        String json = "{ \"response\": {} }";

        List<OwnedGame> games = client.parseOwnedGames(json);

        assertTrue(games.isEmpty());
    }
}
