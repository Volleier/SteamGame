package com.steam.steamapi.dbOperate;

import com.steam.steamapi.pogo.GameList;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class dbSearch {
    static String url = "jdbc:mysql://localhost:3306/game_db";
    static String user = "root";
    static String password = "root";

    public static List<GameList> searchGames() {
        List<GameList> games = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {

            // 查询游戏表中的所有记录
            String sql = "SELECT * FROM games";
            ResultSet rs = stmt.executeQuery(sql);

            // 处理结果集
            while (rs.next()) {
                GameList game = new GameList();
                game.setId(rs.getInt("id"));
                game.setGameAppid(rs.getInt("game_appid"));
                game.setGameName(rs.getString("game_name"));
                game.setGamePlayTime(rs.getInt("game_playtime"));
                games.add(game);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return games;
    }

    public static List<GameList> searchNonZeroGames() {
        List<GameList> games = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {

            // 查询游戏表中的所有非零时长记录
            String sql = "SELECT * FROM games WHERE game_playtime > 0";
            ResultSet rs = stmt.executeQuery(sql);

            // 处理结果集
            while (rs.next()) {
                GameList game = new GameList();
                game.setId(rs.getInt("id"));
                game.setGameAppid(rs.getInt("game_appid"));
                game.setGameName(rs.getString("game_name"));
                game.setGamePlayTime(rs.getInt("game_playtime"));
                games.add(game);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return games;
    }
}
