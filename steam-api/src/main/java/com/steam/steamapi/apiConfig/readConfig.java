package com.steam.steamapi.apiConfig;


import com.steam.steamapi.pogo.User;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class readConfig {
    private static final String CONFIG_FILE = "./steam-api/src/main/java/com/steam/steamapi/apiConfig/LocalUser.config";

    // 检查用户是否存在
    public static boolean checkUserConfig(String user_id) {
        Properties properties = new Properties();

        try (FileInputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);

            // 读取 api_key 和 user_id
            String api_Key_R = properties.getProperty("api_key");
            String user_id_R = properties.getProperty("user_id");

            if (user_id_R != null && api_Key_R != null) {
                if (user_id.equals(user_id_R)) {
                    System.out.println("Success: User exist");
                    return true;
                }
            } else {
                System.out.println("Error: api_key or user_id is missing in the configuration file.");
                return false;
            }
        } catch (IOException e) {
            System.out.println("Error reading the configuration file: " + e.getMessage());
            return false;
        }
        return false;
    }

    public static User readUserConfig() {
        Properties properties = new Properties();
        User user = new User();

        try (FileInputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);

            // 读取 api_key 和 user_id
            String apiKey = properties.getProperty("api_key");
            String userId = properties.getProperty("user_id");

            if (apiKey != null && userId != null) {
                user.setApi_key(apiKey.trim());
                user.setUser_id(userId.trim());
            } else {
                System.out.println("Error: api_key or user_id is missing in the configuration file.");
                return null;
            }
        } catch (IOException e) {
            System.out.println("Error reading the configuration file: " + e.getMessage());
            return null;
        }

        return user;
    }

    // 写入配置文件
    public static void writeUserConfig(String user_id, String api_key) {
        Properties properties = new Properties();
        properties.setProperty("api_key", api_key.trim());
        properties.setProperty("user_id", user_id.trim());

        try (FileOutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.store(output, "Steam User Configuration");
            System.out.println("User configuration saved successfully.");
        } catch (IOException e) {
            System.out.println("Error writing the configuration file: " + e.getMessage());
        }
    }

//    // 测试读取用户配置
//    public static void main(String[] args) {
//        User user = readUserConfig();
//
//        if (user != null) {
//            System.out.println("User configuration loaded successfully:");
//            System.out.println("User ID: " + user.getUser_id());
//            System.out.println("API Key: " + user.getApi_key());
//        } else {
//            System.out.println("No user configuration found. Creating a new user...");
//            // 如果用户不存在，创建一个新用户并写入配置文件
//            User newUser = new User();
//            newUser.setUser_id("");
//            newUser.setApi_key("");
//            writeUserConfig(newUser.getUser_id(), newUser.getApi_key());
//        }
//    }
}