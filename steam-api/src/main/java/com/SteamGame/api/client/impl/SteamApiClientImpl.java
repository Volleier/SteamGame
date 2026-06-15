package com.SteamGame.api.client.impl;

import com.SteamGame.api.client.SteamApiClient;
import com.SteamGame.api.client.steam.SteamWebApiClient;
import com.SteamGame.api.client.store.SteamStoreClient;
import com.SteamGame.api.domain.OwnedGame;
import com.SteamGame.api.dto.metadata.GameMetadataDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Backward-compatible adapter that delegates to the new split clients.
 * Deprecated: migrate callers to use SteamWebApiClient and SteamStoreClient directly.
 */
@Component
@Deprecated
public class SteamApiClientImpl implements SteamApiClient {

    private static final Logger logger = LoggerFactory.getLogger(SteamApiClientImpl.class);

    private final SteamWebApiClient webApiClient;
    private final SteamStoreClient storeClient;

    public SteamApiClientImpl(SteamWebApiClient webApiClient, SteamStoreClient storeClient) {
        this.webApiClient = webApiClient;
        this.storeClient = storeClient;
    }

    @Override
    public List<OwnedGame> fetchOwnedGames(String steamId, String apiKey) throws IOException, InterruptedException {
        return webApiClient.getOwnedGames(steamId, apiKey);
    }

    @Override
    public void fillGameDetails(OwnedGame game) {
        try {
            GameMetadataDTO dto = storeClient.getAppDetails(game.getAppid(), null, null);
            if (dto != null) {
                if (dto.getDevelopers() != null && !dto.getDevelopers().isEmpty()) {
                    game.setDeveloper(dto.getDevelopers().get(0));
                }
                if (dto.getPublishers() != null && !dto.getPublishers().isEmpty()) {
                    game.setPublisher(dto.getPublishers().get(0));
                }
                game.setReleaseDate(dto.getReleaseDate());

                // build tags from categories + genres
                List<String> tagList = new ArrayList<>();
                if (dto.getCategories() != null) {
                    for (var cat : dto.getCategories()) {
                        if (cat.getDescription() != null && !cat.getDescription().isEmpty()) {
                            tagList.add(cat.getDescription());
                        }
                    }
                }
                if (dto.getGenres() != null) {
                    for (var gen : dto.getGenres()) {
                        if (gen.getDescription() != null && !gen.getDescription().isEmpty() && !tagList.contains(gen.getDescription())) {
                            tagList.add(gen.getDescription());
                        }
                    }
                }
                if (!tagList.isEmpty()) {
                    game.setTags(String.join(",", tagList));
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to fetch game details for appid {}: {}", game.getAppid(), e.getMessage());
        }
    }
}
