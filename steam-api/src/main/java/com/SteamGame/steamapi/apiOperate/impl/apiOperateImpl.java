package com.SteamGame.steamapi.apiOperate.impl;

import com.SteamGame.steamapi.apiOperate.apiOperate;
import com.SteamGame.steamapi.pogo.GameList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class apiOperateImpl implements apiOperate {
    private static final String API_URL = "https://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/";
    private static final int MAX_RETRIES = 3;
    private static final int TIMEOUT = 5000; // 5 seconds

    @Override
    public List<GameList> getGameList(String apiKey, String steamId) {
        List<GameList> games = new ArrayList<>();
        int attempts = 0;

        while (attempts < MAX_RETRIES) {
            try {
                String urlString = API_URL + "?key=" + apiKey + "&steamid=" + steamId + "&format=json&include_appinfo=true";
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(TIMEOUT);
                conn.setReadTimeout(TIMEOUT);

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    JSONObject responseObject = ConnectURL(conn);
                    if (responseObject.has("games")) {
                        JSONArray gamesArray = responseObject.getJSONArray("games");
                        for (int i = 0; i < gamesArray.length(); i++) {
                            JSONObject game = gamesArray.getJSONObject(i);
                            GameList gameList = new GameList();
                            gameList.setGameAppid(Integer.valueOf(game.getString("appid")));
                            gameList.setGameName(game.getString("name"));
                            gameList.setGamePlayTime(Integer.valueOf(String.valueOf(game.getInt("playtime_forever"))));
                            games.add(gameList);
                        }
                    }
                    break; // Exit loop if successful
                } else {
                    System.out.println("Failed to fetch data. HTTP response code: " + responseCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
                attempts++;
                if (attempts >= MAX_RETRIES) {
                    System.out.println("Max retries reached. Unable to fetch data.");
                }
            }
        }
        return games;
    }

    public static JSONObject ConnectURL(HttpURLConnection conn) throws IOException, JSONException {
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        JSONObject jsonResponse = new JSONObject(response.toString());
        return jsonResponse.getJSONObject("response");
    }
}