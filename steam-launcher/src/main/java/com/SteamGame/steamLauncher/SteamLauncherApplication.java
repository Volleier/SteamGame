package com.SteamGame.steamLauncher;

import com.SteamGame.login.controller.CredentialConfigController;
import com.SteamGame.login.controller.CredentialVerifyController;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.SteamGame")
@MapperScan("com.SteamGame.api.mapper")
public class SteamLauncherApplication {

    private static final Logger logger = LoggerFactory.getLogger(SteamLauncherApplication.class);

    public static void main(String[] args) {
        // Ensure H2 data directory exists before Spring starts (so DataSource can
        // open/create DB file at ./data/steamdb.mv.db)
        try {
            java.nio.file.Path dataDir = java.nio.file.Paths.get(System.getProperty("user.dir"), "data");
            if (!java.nio.file.Files.exists(dataDir)) {
                java.nio.file.Files.createDirectories(dataDir);
                System.out.println("Created data dir: " + dataDir.toAbsolutePath());
            }
        } catch (Exception e) {
            System.out.println("Failed to create data dir before start: " + e.getMessage());
        }

        SpringApplication.run(SteamLauncherApplication.class, args);
        logger.info("SteamGame应用程序启动完毕");
    }

    @Bean
    public CommandLineRunner initLoginModule(CredentialVerifyController loginController,
            CredentialConfigController registerController,
            javax.sql.DataSource dataSource) {
        return args -> {
            logger.info("正在初始化凭据验证模块...");
            loginController.init();
            logger.info("凭据验证模块初始化完成");

            logger.info("正在初始化凭据配置模块...");
            registerController.init();
            logger.info("凭据配置模块初始化完成");

            // 打印实际 DataSource 信息以便调试
            try {
                try (java.sql.Connection c = dataSource.getConnection()) {
                    String url = c.getMetaData().getURL();
                    String user = c.getMetaData().getUserName();
                    logger.info("Effective DataSource URL: {}", url);
                    logger.info("Effective DB user: {}", user);
                }
            } catch (Exception e) {
                logger.warn("无法获取 DataSource 连接以打印 JDBC URL: {}", e.getMessage());
            }
        };
    }
}