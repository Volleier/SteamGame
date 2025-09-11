package com.SteamGame.common;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CommonApplication {

    @Bean(name = "commonModuleInitializer")
    public String commonModuleInitializer() {
        return "Common module initialized";
    }

    public static void main(String[] args) {
        SpringApplication.run(CommonApplication.class, args);
    }
}