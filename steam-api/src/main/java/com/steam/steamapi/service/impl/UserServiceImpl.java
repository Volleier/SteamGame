package com.steam.steamapi.service.impl;

import com.steam.steamapi.apiConfig.readConfig;
import com.steam.steamapi.pogo.User;
import com.steam.steamapi.service.UserService;
import com.steam.steamapi.utils.Md5Util;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class UserServiceImpl implements UserService {

    private readConfig readConfig;

    @Override
    public User checkUser(String username) throws IOException {
        User u = com.steam.steamapi.apiConfig.readConfig.readUserConfig();
        return null;
    }

    @Override
    public void register(String username, String password) {
        String md5password = Md5Util.getMd5(password);
        com.steam.steamapi.apiConfig.readConfig.writeUserConfig(username, md5password);
    }
}
