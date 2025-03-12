package com.steam.steamapi.dbOperate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class dbSearch {
      public static void searchGames() {
        String url = "jdbc:mysql://localhost:3306/game_db";
        String user = "root";
        String password = "root";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {

            System.out.println("Connected to the database successfully.");

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

                System.out.println("ID: " + id + ", AppID: " + gameAppid + ", Name: " + gameName + ", Playtime: " + gamePlaytime);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
