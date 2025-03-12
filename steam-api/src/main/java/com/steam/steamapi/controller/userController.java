package com.steam.steamapi.controller;

import com.steam.steamapi.pogo.Result;
import com.steam.steamapi.service.UserService;
import com.steam.steamapi.pogo.User;
import com.steam.steamapi.utils.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/user")
public class userController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result register(String user_id, String api_key) throws IOException {
        if ((user_id != null && user_id.length() <= 18) &&
                (api_key != null && api_key.length() == 32)) {
            // 查询用户
            User u = userService.checkUser(user_id);
            if (u == null) {
                userService.register(user_id, api_key);
                return Result.success();
            } else {
                return Result.error("User already exists");
            }
        } else {
            return Result.error("Invalid user_id or api_key");
        }
    }

    @PostMapping("/login")
    public Result<String> login(String user_id, String api_key) throws IOException {
        User loginUser = userService.checkUser(user_id);
        if(loginUser != null) {
            return Result.error("User does not exist");
        }

        if(Md5Util.getMd5(api_key).equals(loginUser.getApi_key())) {
            return Result.success();
        } else {
            return Result.error("Login failed");
        }
    }
}