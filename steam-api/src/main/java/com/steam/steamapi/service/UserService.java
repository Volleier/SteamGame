package com.steam.steamapi.service;

import com.steam.steamapi.pogo.User;

import java.io.IOException;

public interface UserService {
    User checkUser(String username) throws IOException;

    void register(String username, String password);
}
