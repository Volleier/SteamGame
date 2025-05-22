package com.SteamGame.steamLauncher;

import com.SteamGame.login.controller.RegisterController;
import com.SteamGame.login.controller.LoginController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan({"com.SteamGame.steamLauncher", "com.SteamGame.login"})
public class SteamLauncherApplication {

    private static final Logger logger = LoggerFactory.getLogger(SteamLauncherApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SteamLauncherApplication.class, args);
        logger.info("SteamGame应用程序启动完毕");
    }

    @Bean
    public CommandLineRunner initLoginModule(LoginController loginController) {
        return args -> {
            logger.info("正在初始化登录模块...");
            loginController.login();
            logger.info("登录模块初始化完成");
        };
    }
}