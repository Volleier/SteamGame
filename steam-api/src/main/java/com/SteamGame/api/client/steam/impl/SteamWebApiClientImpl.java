package com.SteamGame.api.client.steam.impl;

import com.SteamGame.api.client.steam.SteamWebApiClient;
import com.SteamGame.api.config.SteamHttpClientConfig;
import com.SteamGame.api.domain.OwnedGame;
import com.SteamGame.api.dto.SteamApiSupportedListDTO;
import com.SteamGame.api.dto.achievement.AchievementGlobalPercentDTO;
import com.SteamGame.api.dto.news.GameNewsDTO;
import com.SteamGame.api.dto.news.GameNewsResultDTO;
import com.SteamGame.api.dto.player.PlayerFriendDTO;
import com.SteamGame.api.dto.player.PlayerProfileDTO;
import com.SteamGame.api.dto.player.RecentGameDTO;
import com.SteamGame.api.dto.player.WishlistItemDTO;
import com.SteamGame.api.dto.stats.CurrentPlayerCountDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class SteamWebApiClientImpl implements SteamWebApiClient {

    private static final Logger log = LoggerFactory.getLogger(SteamWebApiClientImpl.class);

    private final HttpClient httpClient;
    private final SteamHttpClientConfig config;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SteamWebApiClientImpl(HttpClient steamHttpClient, SteamHttpClientConfig config) {
        this.httpClient = steamHttpClient;
        this.config = config;
    }

    // ──────────────────────────────────────────────
    // ISteamUser/GetPlayerSummaries/v2
    // ──────────────────────────────────────────────
    @Override
    public PlayerProfileDTO getPlayerSummary(String steamId, String apiKey) throws IOException, InterruptedException {
        String url = String.format(
                "https://api.steampowered.com/ISteamUser/GetPlayerSummaries/v2/?key=%s&steamids=%s&format=json",
                encode(apiKey), encode(steamId));
        log.info("SteamWebApiClient: GetPlayerSummaries for steamId={}", steamId);

        String body = get(url, config.getTimeoutSeconds());
        JsonNode players = objectMapper.readTree(body).path("response").path("players");
        if (players.isArray() && players.size() > 0) {
            return parsePlayerProfile(players.get(0));
        }
        return null;
    }

    // ──────────────────────────────────────────────
    // IPlayerService/GetOwnedGames/v1
    // ──────────────────────────────────────────────
    @Override
    public List<OwnedGame> getOwnedGames(String steamId, String apiKey) throws IOException, InterruptedException {
        if (steamId == null || steamId.isEmpty() || apiKey == null || apiKey.isEmpty()) {
            throw new IllegalArgumentException("steamId and apiKey are required");
        }

        String url = String.format(
                "https://api.steampowered.com/IPlayerService/GetOwnedGames/v1/?key=%s&steamid=%s&include_appinfo=1&include_played_free_games=1&format=json",
                encode(apiKey), encode(steamId));

        log.info("SteamWebApiClient: GetOwnedGames for steamId={}", steamId);
        String body = getWithRetry(url, config.getTimeoutSeconds());

        return parseOwnedGames(body);
    }

    // ──────────────────────────────────────────────
    // IPlayerService/GetRecentlyPlayedGames/v1
    // ──────────────────────────────────────────────
    @Override
    public List<RecentGameDTO> getRecentlyPlayedGames(String steamId, String apiKey, int count) throws IOException, InterruptedException {
        String url = String.format(
                "https://api.steampowered.com/IPlayerService/GetRecentlyPlayedGames/v1/?key=%s&steamid=%s&count=%d&format=json",
                encode(apiKey), encode(steamId), count);
        log.info("SteamWebApiClient: GetRecentlyPlayedGames for steamId={}", steamId);

        String body = get(url, config.getTimeoutSeconds());
        return parseRecentGames(body);
    }

    // ──────────────────────────────────────────────
    // ISteamUserStats/GetNumberOfCurrentPlayers/v1
    // ──────────────────────────────────────────────
    @Override
    public CurrentPlayerCountDTO getCurrentPlayers(long appid) throws IOException, InterruptedException {
        String url = String.format(
                "https://api.steampowered.com/ISteamUserStats/GetNumberOfCurrentPlayers/v1/?appid=%d&format=json", appid);
        log.info("SteamWebApiClient: GetNumberOfCurrentPlayers for appid={}", appid);

        String body = get(url, config.getTimeoutSeconds());
        JsonNode resp = objectMapper.readTree(body).path("response");
        CurrentPlayerCountDTO dto = new CurrentPlayerCountDTO();
        dto.setAppid(appid);
        dto.setPlayerCount(resp.path("player_count").asInt(0));
        dto.setCached(false);
        dto.setStale(false);
        return dto;
    }

    // ──────────────────────────────────────────────
    // ISteamUserStats/GetGlobalAchievementPercentagesForApp/v2
    // ──────────────────────────────────────────────
    @Override
    public List<AchievementGlobalPercentDTO> getGlobalAchievementPercentages(long appid) throws IOException, InterruptedException {
        String url = String.format(
                "https://api.steampowered.com/ISteamUserStats/GetGlobalAchievementPercentagesForApp/v2/?gameid=%d&format=json", appid);
        log.info("SteamWebApiClient: GetGlobalAchievementPercentages for appid={}", appid);

        String body = get(url, config.getTimeoutSeconds());
        return parseAchievementPercentages(body);
    }

    // ──────────────────────────────────────────────
    // ISteamNews/GetNewsForApp/v2
    // ──────────────────────────────────────────────
    @Override
    public GameNewsResultDTO getNewsForApp(long appid, int count, Integer maxLength) throws IOException, InterruptedException {
        StringBuilder urlBuilder = new StringBuilder(String.format(
                "https://api.steampowered.com/ISteamNews/GetNewsForApp/v2/?appid=%d&count=%d&format=json",
                appid, count));
        if (maxLength != null) {
            urlBuilder.append("&maxlength=").append(maxLength);
        }
        String url = urlBuilder.toString();
        log.info("SteamWebApiClient: GetNewsForApp for appid={}", appid);

        String body = get(url, config.getTimeoutSeconds());
        return parseNews(body, appid);
    }

    // ──────────────────────────────────────────────
    // ISteamUser/GetFriendList/v1
    // ──────────────────────────────────────────────
    @Override
    public List<PlayerFriendDTO> getFriendList(String steamId, String apiKey) throws IOException, InterruptedException {
        String url = String.format(
                "https://api.steampowered.com/ISteamUser/GetFriendList/v1/?key=%s&steamid=%s&relationship=friend&format=json",
                encode(apiKey), encode(steamId));
        log.info("SteamWebApiClient: GetFriendList for steamId={}", steamId);

        String body = get(url, config.getTimeoutSeconds());
        return parseFriendList(body);
    }

    @Override
    public List<WishlistItemDTO> getWishlist(String steamId, String apiKey) throws IOException, InterruptedException {
        String url = String.format(
                "https://api.steampowered.com/IWishlistService/GetWishlist/v1/?key=%s&steamid=%s&format=json",
                encode(apiKey), encode(steamId));
        log.info("SteamWebApiClient: GetWishlist for steamId={}", steamId);

        String body = get(url, config.getTimeoutSeconds());
        return parseWishlist(body);
    }

    // ──────────────────────────────────────────────
    // ISteamWebAPIUtil/GetSupportedAPIList/v1
    // ──────────────────────────────────────────────
    @Override
    public SteamApiSupportedListDTO getSupportedApiList(String apiKeyOrNull) throws IOException, InterruptedException {
        String url;
        if (apiKeyOrNull != null && !apiKeyOrNull.isEmpty()) {
            url = String.format(
                    "https://api.steampowered.com/ISteamWebAPIUtil/GetSupportedAPIList/v1/?key=%s&format=json",
                    encode(apiKeyOrNull));
        } else {
            url = "https://api.steampowered.com/ISteamWebAPIUtil/GetSupportedAPIList/v1/?format=json";
        }
        log.info("SteamWebApiClient: GetSupportedAPIList withKey={}", apiKeyOrNull != null);

        String body = get(url, config.getTimeoutSeconds());
        return parseSupportedApiList(body, apiKeyOrNull != null);
    }

    // ──────────────────────────────────────────────
    // HTTP helpers
    // ──────────────────────────────────────────────

    private String get(String url, int timeoutSeconds) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("User-Agent", "SteamGameClient/1.0")
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .build();
        HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() != 200) {
            throw new IOException("Steam API returned HTTP " + resp.statusCode() + " for URL (key masked)");
        }
        return resp.body();
    }

    private String getWithRetry(String url, int timeoutSeconds) throws IOException, InterruptedException {
        for (int attempt = 1; attempt <= 2; attempt++) {
            try {
                return get(url, timeoutSeconds);
            } catch (java.io.EOFException eof) {
                log.warn("SteamWebApiClient: EOF on attempt {}, will retry", attempt);
                if (attempt >= 2) throw eof;
            }
        }
        throw new IOException("Steam API request failed after retries");
    }

    private static String encode(String s) {
        try { return URLEncoder.encode(s, StandardCharsets.UTF_8); }
        catch (Exception e) { return s; }
    }

    // ──────────────────────────────────────────────
    // JSON parsers
    // ──────────────────────────────────────────────

    private PlayerProfileDTO parsePlayerProfile(JsonNode node) {
        PlayerProfileDTO dto = new PlayerProfileDTO();
        dto.setSteamId(node.path("steamid").asText());
        dto.setPersonaName(node.path("personaname").asText(null));
        dto.setProfileUrl(node.path("profileurl").asText(null));
        dto.setAvatar(node.path("avatar").asText(null));
        dto.setAvatarMedium(node.path("avatarmedium").asText(null));
        dto.setAvatarFull(node.path("avatarfull").asText(null));
        dto.setPersonaState(node.path("personastate").asInt(0));
        dto.setCommunityVisibilityState(node.path("communityvisibilitystate").asInt(0));
        dto.setLastLogoff(node.has("lastlogoff") ? node.path("lastlogoff").asLong() : null);
        dto.setTimeCreated(node.has("timecreated") ? node.path("timecreated").asLong() : null);
        dto.setCountryCode(node.has("loccountrycode") ? node.path("loccountrycode").asText(null) : null);
        return dto;
    }

    public List<OwnedGame> parseOwnedGames(String body) throws IOException {
        List<OwnedGame> games = new ArrayList<>();
        JsonNode root = objectMapper.readTree(body);
        JsonNode gamesNode = root.path("response").path("games");
        if (gamesNode.isArray()) {
            for (JsonNode g : gamesNode) {
                OwnedGame game = new OwnedGame();
                game.setAppid(g.path("appid").asLong());
                game.setName(g.path("name").asText(null));
                game.setPlaytimeForever(g.path("playtime_forever").asInt(0));
                // extended fields
                if (g.has("playtime_2weeks")) game.setPlaytime2weeks(g.path("playtime_2weeks").asInt(0));
                if (g.has("rtime_last_played")) game.setRtimeLastPlayed(g.path("rtime_last_played").asLong());
                if (g.has("img_icon_url")) game.setImgIconUrl(g.path("img_icon_url").asText(null));
                if (g.has("has_community_visible_stats")) game.setHasCommunityVisibleStats(g.path("has_community_visible_stats").asBoolean());
                if (g.has("playtime_windows_forever")) game.setPlaytimeWindowsForever(g.path("playtime_windows_forever").asInt(0));
                if (g.has("playtime_mac_forever")) game.setPlaytimeMacForever(g.path("playtime_mac_forever").asInt(0));
                if (g.has("playtime_linux_forever")) game.setPlaytimeLinuxForever(g.path("playtime_linux_forever").asInt(0));
                if (g.has("playtime_deck_forever")) game.setPlaytimeDeckForever(g.path("playtime_deck_forever").asInt(0));
                games.add(game);
            }
        }
        log.info("SteamWebApiClient: parsed {} owned games", games.size());
        return games;
    }

    private List<RecentGameDTO> parseRecentGames(String body) throws IOException {
        List<RecentGameDTO> games = new ArrayList<>();
        JsonNode gamesNode = objectMapper.readTree(body).path("response").path("games");
        if (gamesNode.isArray()) {
            for (JsonNode g : gamesNode) {
                RecentGameDTO dto = new RecentGameDTO();
                dto.setAppid(g.path("appid").asLong());
                dto.setName(g.path("name").asText(null));
                dto.setPlaytime2Weeks(g.path("playtime_2weeks").asInt(0));
                dto.setPlaytimeForever(g.path("playtime_forever").asInt(0));
                dto.setIconUrl(g.path("img_icon_url").asText(null));
                games.add(dto);
            }
        }
        return games;
    }

    private List<AchievementGlobalPercentDTO> parseAchievementPercentages(String body) throws IOException {
        List<AchievementGlobalPercentDTO> list = new ArrayList<>();
        JsonNode achievements = objectMapper.readTree(body).path("achievementpercentages").path("achievements");
        if (achievements.isArray()) {
            for (JsonNode a : achievements) {
                AchievementGlobalPercentDTO dto = new AchievementGlobalPercentDTO();
                dto.setName(a.path("name").asText());
                dto.setPercent(a.path("percent").asDouble());
                list.add(dto);
            }
        }
        return list;
    }

    private GameNewsResultDTO parseNews(String body, long appid) throws IOException {
        JsonNode appnews = objectMapper.readTree(body).path("appnews");
        GameNewsResultDTO result = new GameNewsResultDTO();
        result.setAppid(appid);
        List<GameNewsDTO> items = new ArrayList<>();
        JsonNode newsitems = appnews.path("newsitems");
        if (newsitems.isArray()) {
            for (JsonNode n : newsitems) {
                GameNewsDTO dto = new GameNewsDTO();
                dto.setGid(n.path("gid").asText());
                dto.setTitle(n.path("title").asText(null));
                dto.setUrl(n.path("url").asText(null));
                dto.setIsExternalUrl(n.path("is_external_url").asBoolean());
                dto.setAuthor(n.path("author").asText(null));
                dto.setContents(n.path("contents").asText(null));
                dto.setFeedLabel(n.path("feedlabel").asText(null));
                dto.setDate(n.path("date").asLong());
                items.add(dto);
            }
        }
        result.setItems(items);
        return result;
    }

    private List<PlayerFriendDTO> parseFriendList(String body) throws IOException {
        List<PlayerFriendDTO> list = new ArrayList<>();
        JsonNode friends = objectMapper.readTree(body).path("friendslist").path("friends");
        if (friends.isArray()) {
            for (JsonNode f : friends) {
                PlayerFriendDTO dto = new PlayerFriendDTO();
                dto.setSteamId(f.path("steamid").asText());
                dto.setRelationship(f.path("relationship").asText(null));
                dto.setFriendSince(f.path("friend_since").asLong());
                list.add(dto);
            }
        }
        return list;
    }

    public List<WishlistItemDTO> parseWishlist(String body) throws IOException {
        List<WishlistItemDTO> list = new ArrayList<>();
        JsonNode root = objectMapper.readTree(body);
        JsonNode items = root.path("response").path("items");
        if (!items.isArray()) {
            items = root.path("response").path("wishlist");
        }
        if (!items.isArray()) {
            items = root.path("items");
        }
        if (items.isArray()) {
            for (JsonNode item : items) {
                WishlistItemDTO dto = new WishlistItemDTO();
                dto.setAppid(item.path("appid").asLong());
                dto.setName(item.path("name").asText(null));
                dto.setPriority(item.has("priority") ? item.path("priority").asInt() : null);
                if (item.has("date_added")) {
                    dto.setAddedAt(item.path("date_added").asLong());
                } else if (item.has("added_at")) {
                    dto.setAddedAt(item.path("added_at").asLong());
                } else if (item.has("time_added")) {
                    dto.setAddedAt(item.path("time_added").asLong());
                }
                if (dto.getAppid() != null && dto.getAppid() > 0) {
                    list.add(dto);
                }
            }
        }
        return list;
    }

    private SteamApiSupportedListDTO parseSupportedApiList(String body, boolean withKey) throws IOException {
        JsonNode root = objectMapper.readTree(body);
        SteamApiSupportedListDTO dto = new SteamApiSupportedListDTO();
        dto.setSource("ISteamWebAPIUtil/GetSupportedAPIList");
        dto.setWithKey(withKey);
        List<SteamApiSupportedListDTO.InterfaceDTO> interfaces = new ArrayList<>();
        JsonNode apilist = root.path("apilist").path("interfaces");
        if (apilist.isArray()) {
            for (JsonNode iface : apilist) {
                SteamApiSupportedListDTO.InterfaceDTO ifaceDto = new SteamApiSupportedListDTO.InterfaceDTO();
                ifaceDto.setName(iface.path("name").asText());
                List<SteamApiSupportedListDTO.MethodDTO> methods = new ArrayList<>();
                JsonNode methArr = iface.path("methods");
                if (methArr.isArray()) {
                    for (JsonNode m : methArr) {
                        SteamApiSupportedListDTO.MethodDTO methDto = new SteamApiSupportedListDTO.MethodDTO();
                        methDto.setName(m.path("name").asText());
                        methDto.setVersion(m.path("version").asInt());
                        methDto.setHttpMethod(m.path("httpmethod").asText());
                        List<SteamApiSupportedListDTO.ParameterDTO> params = new ArrayList<>();
                        JsonNode paramArr = m.path("parameters");
                        if (paramArr.isArray()) {
                            for (JsonNode p : paramArr) {
                                SteamApiSupportedListDTO.ParameterDTO paramDto = new SteamApiSupportedListDTO.ParameterDTO();
                                paramDto.setName(p.path("name").asText(null));
                                paramDto.setType(p.path("type").asText(null));
                                paramDto.setOptional(p.path("optional").asBoolean());
                                paramDto.setDescription(p.path("description").asText(null));
                                params.add(paramDto);
                            }
                        }
                        methDto.setParameters(params);
                        methods.add(methDto);
                    }
                }
                ifaceDto.setMethods(methods);
                interfaces.add(ifaceDto);
            }
        }
        dto.setInterfaces(interfaces);
        return dto;
    }
}
