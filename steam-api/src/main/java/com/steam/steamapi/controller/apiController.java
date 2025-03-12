package com.steam.steamapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@RestController
public class apiController {
    private static final String API_URL = "http://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/";

    @GetMapping("/getOwnedGames")
    public List<String> getOwnedGames(@RequestParam String apiKey, @RequestParam String steamId) {
        List<String> gameNames = new ArrayList<>();

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
                        String gameName = game.getString("name");
                        gameNames.add(gameName);
                    }
                }
            } else {
                System.out.println("Failed to fetch data. HTTP response code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return gameNames;
    }
}