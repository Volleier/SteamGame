package com.SteamGame.steamapi.dbOperate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class dbCreate {
    public static void createDatabase() {
        String url = "jdbc:mysql://localhost:3306/";
        String user = "root";
        String password = "root";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {

            // 创建数据库
            String sql = "CREATE DATABASE IF NOT EXISTS game_db";
            stmt.executeUpdate(sql);

            // 使用数据库
            sql = "USE game_db";
            stmt.executeUpdate(sql);

            // 创建表
            sql = "CREATE TABLE IF NOT EXISTS games (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "game_appid INT NOT NULL, " +
                    "game_name VARCHAR(255) NOT NULL, " +
                    "game_playtime INT)";
            stmt.executeUpdate(sql);

            System.out.println("Database and table created successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
