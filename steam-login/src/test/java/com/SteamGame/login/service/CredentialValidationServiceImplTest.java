package com.SteamGame.login.service;

import com.SteamGame.login.dto.CredentialCheckResult;
import com.SteamGame.login.service.impl.CredentialValidationServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.net.SocketTimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class CredentialValidationServiceImplTest {

    @Test
    void validatesOnlineCredentialAndCountsOwnedGames() throws Exception {
        CredentialValidationServiceImpl service = new CredentialValidationServiceImpl(15);
        RestTemplate restTemplate = (RestTemplate) ReflectionTestUtils.getField(service, "restTemplate");
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();

        String steamId = "76561198000000000";
        String apiKey = "TEST_API_KEY";
        server.expect(once(), requestTo(
                        "https://api.steampowered.com/ISteamUser/GetPlayerSummaries/v2/?key=" + apiKey + "&steamids=" + steamId))
                .andRespond(withSuccess("""
                        {"response":{"players":[{"steamid":"76561198000000000"}]}}
                        """, MediaType.APPLICATION_JSON));
        server.expect(once(), requestTo(
                        "https://api.steampowered.com/IPlayerService/GetOwnedGames/v1/?key=" + apiKey + "&steamid=" + steamId + "&include_played_free_games=1&format=json"))
                .andRespond(withSuccess("""
                        {"response":{"game_count":2,"games":[{"appid":1},{"appid":2}]}}
                        """, MediaType.APPLICATION_JSON));

        CredentialCheckResult result = service.validateOnline(steamId, apiKey);

        assertTrue(result.isValidKeyAndUser());
        assertTrue(result.isOwnsGames());
        assertEquals(2, result.getGameCount());
        server.verify();
    }

    @Test
    void mapsSteamTimeoutToExplicitExceptionCode() {
        CredentialValidationServiceImpl service = new CredentialValidationServiceImpl(15);
        RestTemplate restTemplate = (RestTemplate) ReflectionTestUtils.getField(service, "restTemplate");
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();

        server.expect(once(), requestTo(
                        "https://api.steampowered.com/ISteamUser/GetPlayerSummaries/v2/?key=TEST_API_KEY&steamids=76561198000000000"))
                .andRespond(withException(new SocketTimeoutException("timeout")));

        Exception ex = assertThrows(Exception.class,
                () -> service.validateOnline("76561198000000000", "TEST_API_KEY"));

        assertEquals("STEAM_API_TIMEOUT", ex.getMessage());
        assertTrue(ex.getCause() instanceof org.springframework.web.client.ResourceAccessException);
        server.verify();
    }
}
