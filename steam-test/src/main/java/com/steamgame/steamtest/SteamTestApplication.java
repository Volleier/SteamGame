package com.steamgame.steamtest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.steamgame.steamtest.startup.StartupRunner;

@SpringBootApplication
public class SteamTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(SteamTestApplication.class, args);
    }

    @Bean
    public StartupRunner startupRunner(com.steamgame.steamtest.service.SteamService steamService) {
    // Create a StartupRunner that will perform simple smoke tests when
    // the application is ready. The StartupRunner is intentionally kept
    // synchronous to make startup issues visible in the logs.
    return new StartupRunner(steamService);
    }

}
