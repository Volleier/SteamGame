package com.SteamGame.login.service.impl;

import com.SteamGame.util.ValidationUtil;
import com.SteamGame.login.service.LoginService;
import com.SteamGame.login.dto.LoginDTO;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {

    @Override
    public boolean receiveLoginInfo(LoginDTO loginDTO) {
        if (ValidationUtil.isValidLoginInfo(loginDTO)) {
            System.out.println("Login information received: " + loginDTO);
            return true;
        } else {
            System.out.println("Invalid login information.");
            return false;
        }
    }

    @Override
    public boolean saveLoginInfo(LoginDTO loginDTO) {

        return false;
    }

    @Override
    public void sayHello() {
        System.out.println("Hello from LoginServiceImpl!!!");
    }
}
