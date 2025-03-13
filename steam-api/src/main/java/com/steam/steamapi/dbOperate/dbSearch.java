package com.steam.steamapi.dbOperate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class dbSearch {
    static String url = "jdbc:mysql://localhost:3306/game_db";
    static String user = "root";
    static String password = "root";

    public static void searchGames() {
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {

            // 查询游戏表中的所有记录
            String sql = "SELECT * FROM games";
            ResultSet rs = stmt.executeQuery(sql);

            // 检查是否有结果
            if (!rs.isBeforeFirst()) {
                System.out.println("No data found.");
                return;
            }

            // 处理结果集
            while (rs.next()) {
                int id = rs.getInt("id");
                int gameAppid = rs.getInt("game_appid");
                String gameName = rs.getString("game_name");
                int gamePlaytime = rs.getInt("game_playtime");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void searchNonZeroGames() {
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {

            // 查询游戏表中的所有非零时长记录
            String sql = "SELECT * FROM games WHERE game_playtime > 0";
            ResultSet rs = stmt.executeQuery(sql);

            // 检查是否有结果
            if (!rs.isBeforeFirst()) {
                System.out.println("No data found.");
                return;
            }

            // 处理结果集
            while (rs.next()) {
                int id = rs.getInt("id");
                int gameAppid = rs.getInt("game_appid");
                String gameName = rs.getString("game_name");
                int gamePlaytime = rs.getInt("game_playtime");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
