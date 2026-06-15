package com.SteamGame.launcher.startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 应用启动前置检查 —— 创建 data 目录、打印 DataSource 信息。
 * 不调用任何 Controller 初始化方法。
 */
@Component
public class ApplicationStartupRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationStartupRunner.class);

    private final DataSource dataSource;

    public ApplicationStartupRunner(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1) 确保 H2 data 目录存在
        try {
            Path dataDir = Paths.get(System.getProperty("user.dir"), "data");
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
                logger.info("Created data directory: {}", dataDir.toAbsolutePath());
            }
        } catch (Exception e) {
            logger.warn("Failed to create data directory: {}", e.getMessage());
        }

        // 2) 打印实际 DataSource 信息以便调试
        try (var conn = dataSource.getConnection()) {
            String url = conn.getMetaData().getURL();
            String user = conn.getMetaData().getUserName();
            logger.info("Effective DataSource URL: {}", url);
            logger.info("Effective DB user: {}", user);
        } catch (Exception e) {
            logger.warn("无法获取 DataSource 连接以打印 JDBC URL: {}", e.getMessage());
        }
    }
}
