package com.steam.steamapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class SteamApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(SteamApiApplication.class, args);
        
    }

}
