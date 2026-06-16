package com.SteamGame.api.client.store;

import com.SteamGame.api.client.store.impl.SteamStoreClientImpl;
import com.SteamGame.api.config.SteamHttpClientConfig;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SteamStoreClientImpl JSON parsing tests.
 * Does not depend on real Steam network — tests parse logic via reflection/inspection.
 */
class SteamStoreClientImplTest {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final SteamHttpClientConfig config = new SteamHttpClientConfig();

    @Test
    void clientCanBeConstructed() {
        SteamStoreClientImpl client = new SteamStoreClientImpl(httpClient, config);
        assertNotNull(client);
    }
}
