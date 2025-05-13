package com.SteamGame.steamapi.service.impl;

import com.SteamGame.steamapi.apiConfig.readConfig;
import com.SteamGame.steamapi.pogo.User;
import com.SteamGame.steamapi.service.UserService;
import com.SteamGame.steamapi.utils.Md5Util;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class UserServiceImpl implements UserService {

    private readConfig readConfig;

    @Override
    public User checkUser(String username) throws IOException {
        User u = com.SteamGame.steamapi.apiConfig.readConfig.readUserConfig();
        return null;
    }

    @Override
    public void register(String username, String password) {
        String md5password = Md5Util.getMd5(password);
        com.SteamGame.steamapi.apiConfig.readConfig.writeUserConfig(username, md5password);
    }
}
