package com.SteamGame.login;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import com.SteamGame.common.CommonApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@Import(CommonApplication.class)
@DependsOn("commonModuleInitializer")
public class LoginApplication {
    public static void main(String[] args) {
        SpringApplication.run(LoginApplication.class, args);
    }
}