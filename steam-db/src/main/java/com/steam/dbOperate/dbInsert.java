package com.steam.dbOperate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import com.steam.steamapi.pogo.GameList;

public class dbInsert {
    public static void insertGame(int gameAppid, String gameName, int gamePlaytime) {
        String url = "jdbc:mysql://localhost:3306/game_db";
        String user = "root";
        String password = "root";

        String sql = "INSERT INTO games (game_appid, game_name, game_playtime) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, gameAppid);
            pstmt.setString(2, gameName);
            pstmt.setInt(3, gamePlaytime);

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new game was inserted successfully!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insertGame(com.steam.steamapi.pogo.GameList game) {
        String url = "jdbc:mysql://localhost:3306/game_db";
        String user = "root";
        String password = "root";

        int gameAppid = game.getGameAppid();
        String gameName = game.getGameName();
//        int gamePlaytime = game.getGamePlaytime();


        String sql = "INSERT INTO games (game_appid, game_name, game_playtime) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, gameAppid);
            pstmt.setString(2, gameName);
//            pstmt.setInt(3, gamePlaytime);

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new game was inserted successfully!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
