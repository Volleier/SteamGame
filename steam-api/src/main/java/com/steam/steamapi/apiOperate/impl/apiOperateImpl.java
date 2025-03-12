package com.steam.steamapi.apiOperate.impl;

import com.steam.steamapi.apiOperate.apiOperate;
import com.steam.steamapi.pogo.GameList;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class apiOperateImpl implements apiOperate {
    private static final String API_URL = "https://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/";

    @Override
    public List<GameList> getGame(String apiKey, String steamId) {
        List<GameList> games = new ArrayList<>();

        try {
            String urlString = API_URL + "?key=" + apiKey + "&steamid=" + steamId + "&format=json&include_appinfo=true";
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONObject responseObject = jsonResponse.getJSONObject("response");
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
            } else {
                System.out.println("Failed to fetch data. HTTP response code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return games;
    }
}
