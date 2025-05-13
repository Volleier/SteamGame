package com.SteamGame.steamapi.controller;

import com.SteamGame.steamapi.apiConfig.readConfig;
import com.SteamGame.steamapi.apiOperate.apiOperate;
import com.SteamGame.steamapi.apiOperate.impl.apiOperateImpl;
import com.SteamGame.steamapi.dbOperate.dbInsert;
import com.SteamGame.steamapi.pogo.GameList;
import com.SteamGame.steamapi.pogo.User;

import java.util.List;

public class dbController {
    public static void main(String[] args) {
        // 读取配置文件
        User user = readConfig.readUserConfig();
        if (user == null) {
            System.out.println("Main Error: User configuration file is missing.");
            return;
        }


        // 检查用户是否存在
        if (!readConfig.checkUserConfig(user.getUser_id())) {
            System.out.println("Main Error: User does not exist.");
            return;
        }

        System.out.println("Main -- checkUserConfig");
        System.out.println(user.getUser_id());
        System.out.println(user.getApi_key());

        // 获取用户游戏列表
        apiOperate apiOperate = new apiOperateImpl();
        List<GameList> gameList = apiOperate.getGameList(user.getApi_key(), user.getUser_id());
        if (gameList == null) {
            System.out.println("Main Error: Failed to get the game list.");
            return;
        }

        System.out.println("Main -- getGameList");

        // 插入数据库
        for (GameList game : gameList) {
            dbInsert.insertGame(game);
        }
    }
}
