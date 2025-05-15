package com.SteamGame.steamLauncher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SteamLauncherApplication {
    public static void main(String[] args) {
        SpringApplication.run(SteamLauncherApplication.class, args);
        System.out.println("Start Launcher");
    }
}