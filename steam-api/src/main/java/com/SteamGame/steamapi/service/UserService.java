package com.SteamGame.steamapi.service;

import com.SteamGame.steamapi.pogo.User;

import java.io.IOException;

public interface UserService {
    User checkUser(String username) throws IOException;

    void register(String username, String password);
}
