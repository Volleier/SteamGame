package com.SteamGame.steamLauncher;

import com.SteamGame.login.controller.CredentialConfigController;
import com.SteamGame.login.controller.CredentialVerifyController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({ "com.SteamGame.steamLauncher", "com.SteamGame.login", "com.SteamGame.api" })
public class SteamLauncherApplication {

    private static final Logger logger = LoggerFactory.getLogger(SteamLauncherApplication.class);

    public static void main(String[] args) {
        // Ensure H2 data directory exists before Spring starts (so DataSource can
        // open/create DB file)
        try {
            java.nio.file.Path dataDir = java.nio.file.Paths.get(System.getProperty("user.dir"), "steam-api", "data");
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
            org.springframework.jdbc.core.JdbcTemplate jdbcTemplate, javax.sql.DataSource dataSource) {
        return args -> {
            // 确保H2的数据目录存在，以便可以创建H2文件数据库。
            try {
                java.nio.file.Path dataDir = java.nio.file.Paths.get(System.getProperty("user.dir"), "steam-api",
                        "data");
                if (!java.nio.file.Files.exists(dataDir)) {
                    java.nio.file.Files.createDirectories(dataDir);
                    logger.info("已创建 H2 数据目录: {}", dataDir.toAbsolutePath());
                }
            } catch (Exception e) {
                logger.warn("在启动时尝试创建 data 目录失败: {}", e.getMessage());
            }
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
                    logger.info("Effecti在工作区运行一次ve DataSource URL: {}", url);
                    logger.info("Effective DB user: {}", user);
                }
            } catch (Exception e) {
                logger.warn("无法获取 DataSource 连接以打印 JDBC URL: {}", e.getMessage());
            }

            // 在 launcher 启动时创建 owned_game 表（如果不存在）
            try {
                String ddl = "CREATE TABLE IF NOT EXISTS owned_game ( id BIGINT AUTO_INCREMENT PRIMARY KEY, appid BIGINT, name VARCHAR(512), playtime_forever INT )";
                jdbcTemplate.execute(ddl);
                logger.info("owned_game 表已创建或已存在");
            } catch (Exception e) {
                logger.error("创建 owned_game 表失败: {}", e.getMessage(), e);
            }
        };
    }
}