package com.SteamGame.api.client.store.impl;

import com.SteamGame.api.client.store.SteamStoreClient;
import com.SteamGame.api.config.SteamHttpClientConfig;
import com.SteamGame.api.dto.metadata.GameMetadataDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.sql.Timestamp;

@Component
public class SteamStoreClientImpl implements SteamStoreClient {

    private static final Logger log = LoggerFactory.getLogger(SteamStoreClientImpl.class);

    private final HttpClient httpClient;
    private final SteamHttpClientConfig config;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SteamStoreClientImpl(HttpClient steamHttpClient, SteamHttpClientConfig config) {
        this.httpClient = steamHttpClient;
        this.config = config;
    }

    @Override
    public GameMetadataDTO getAppDetails(long appid, String language, String countryCode) throws IOException, InterruptedException {
        String lang = language != null ? language : config.getStoreLanguage();
        String cc = countryCode != null ? countryCode : config.getStoreCountryCode();
        String url = String.format("https://store.steampowered.com/api/appdetails?appids=%d&l=%s&cc=%s", appid, lang, cc);
        log.info("SteamStoreClient: getAppDetails for appid={}", appid);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .timeout(Duration.ofSeconds(config.getDetailsTimeoutSeconds()))
                .build();

        HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() != 200) {
            throw new IOException("Store API returned HTTP " + resp.statusCode());
        }

        return parseAppDetails(objectMapper.readTree(resp.body()), appid);
    }

    @Override
    public Map<Long, GameMetadataDTO> getAppDetailsBatch(List<Long> appids, String language, String countryCode) throws IOException, InterruptedException {
        Map<Long, GameMetadataDTO> results = new LinkedHashMap<>();
        int count = 0;
        for (Long appid : appids) {
            if (count > 0) {
                Thread.sleep(config.getDetailsDelayMillis());
            }
            try {
                GameMetadataDTO dto = getAppDetails(appid, language, countryCode);
                if (dto != null) {
                    results.put(appid, dto);
                }
            } catch (Exception e) {
                log.warn("SteamStoreClient: failed to fetch appid={}: {}", appid, e.getMessage());
            }
            count++;
        }
        return results;
    }

    private GameMetadataDTO parseAppDetails(JsonNode root, long appid) {
        JsonNode appNode = root.path(String.valueOf(appid));
        if (!appNode.path("success").asBoolean()) return null;

        JsonNode data = appNode.path("data");
        GameMetadataDTO dto = new GameMetadataDTO();
        dto.setAppid(appid);
        dto.setName(data.path("name").asText(null));
        dto.setType(data.path("type").asText(null));
        dto.setShortDescription(data.path("short_description").asText(null));
        dto.setHeaderImage(data.path("header_image").asText(null));
        dto.setCapsuleImage(data.path("capsule_image").asText(null));
        dto.setWebsite(data.path("website").asText(null));

        // developers
        JsonNode devs = data.path("developers");
        if (devs.isArray()) {
            List<String> devList = new ArrayList<>();
            for (JsonNode d : devs) devList.add(d.asText());
            dto.setDevelopers(devList);
        }

        // publishers
        JsonNode pubs = data.path("publishers");
        if (pubs.isArray()) {
            List<String> pubList = new ArrayList<>();
            for (JsonNode p : pubs) pubList.add(p.asText());
            dto.setPublishers(pubList);
        }

        // release_date
        JsonNode relDate = data.path("release_date");
        if (relDate.isObject()) {
            dto.setReleaseDate(relDate.path("date").asText(null));
            dto.setComingSoon(relDate.path("coming_soon").asBoolean());
        }

        // platforms
        JsonNode platforms = data.path("platforms");
        GameMetadataDTO.PlatformsDTO plats = new GameMetadataDTO.PlatformsDTO();
        plats.setWindows(platforms.path("windows").asBoolean());
        plats.setMac(platforms.path("mac").asBoolean());
        plats.setLinux(platforms.path("linux").asBoolean());
        dto.setPlatforms(plats);

        // price
        JsonNode price = data.path("price_overview");
        if (price.isObject()) {
            GameMetadataDTO.PriceDTO p = new GameMetadataDTO.PriceDTO();
            p.setCurrency(price.path("currency").asText(null));
            p.setInitial(price.path("initial").asInt(0));
            p.setFin(price.path("final").asInt(0));
            p.setDiscountPercent(price.path("discount_percent").asInt(0));
            dto.setPrice(p);
        }

        // categories
        JsonNode cats = data.path("categories");
        if (cats.isArray()) {
            List<GameMetadataDTO.CategoryDTO> catList = new ArrayList<>();
            for (JsonNode c : cats) {
                GameMetadataDTO.CategoryDTO cd = new GameMetadataDTO.CategoryDTO();
                cd.setId(c.path("id").asInt());
                cd.setDescription(c.path("description").asText());
                catList.add(cd);
            }
            dto.setCategories(catList);
        }

        // genres
        JsonNode gens = data.path("genres");
        if (gens.isArray()) {
            List<GameMetadataDTO.GenreDTO> genList = new ArrayList<>();
            for (JsonNode g : gens) {
                GameMetadataDTO.GenreDTO gd = new GameMetadataDTO.GenreDTO();
                gd.setId(g.path("id").asText());
                gd.setDescription(g.path("description").asText());
                genList.add(gd);
            }
            dto.setGenres(genList);
        }

        // screenshots
        JsonNode screens = data.path("screenshots");
        if (screens.isArray()) {
            List<GameMetadataDTO.ScreenshotDTO> ssList = new ArrayList<>();
            for (JsonNode s : screens) {
                GameMetadataDTO.ScreenshotDTO sd = new GameMetadataDTO.ScreenshotDTO();
                sd.setId(s.path("id").asInt());
                sd.setPathThumbnail(s.path("path_thumbnail").asText(null));
                sd.setPathFull(s.path("path_full").asText(null));
                ssList.add(sd);
            }
            dto.setScreenshots(ssList);
        }

        dto.setMetadataSource("store_appdetails");
        dto.setMetadataSyncedAt(new Timestamp(System.currentTimeMillis()));
        return dto;
    }
}
