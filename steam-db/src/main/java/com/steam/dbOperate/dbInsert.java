package com.steam.dbOperate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import com.steam.steamapi.pogo.GameList;

public class dbInsert {
    static String url = "jdbc:mysql://localhost:3306/game_db";
    static String user = "root";
    static String password = "root";

    public static void insertGame(int gameAppid, String gameName, int gamePlaytime) {
        insertSqlStatement(url, user, password, gameAppid, gameName, gamePlaytime);
    }

    public static void insertGame(com.steam.steamapi.pogo.GameList game) {


        int gameAppid = game.getGameAppid();
        String gameName = game.getGameName();
        int gamePlaytime = game.getGamePlayTime();


        insertSqlStatement(url, user, password, gameAppid, gameName, gamePlaytime);
    }

    private static void insertSqlStatement(String url, String user, String password, int gameAppid, String gameName, int gamePlaytime) {
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
}
