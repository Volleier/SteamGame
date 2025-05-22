package com.SteamGame.login.service.impl;

import com.SteamGame.login.dto.LoginDTO;
import com.SteamGame.util.ValidationUtil;
import com.SteamGame.login.service.RegisterService;
import org.springframework.stereotype.Service;

@Service
public class RegisterServiceImpl implements RegisterService {

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
}
