package com.SteamGame.launcher;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * SteamGame 唯一启动入口 —— 聚合所有业务模块。
 * 不直接调用任何 Controller，初始化逻辑委托给 {@link ApplicationStartupRunner}。
 */
@SpringBootApplication
@ComponentScan("com.SteamGame")
@MapperScan("com.SteamGame.api.mapper")
public class SteamLauncherApplication {

    private static final Logger logger = LoggerFactory.getLogger(SteamLauncherApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SteamLauncherApplication.class, args);
        logger.info("SteamGame 应用程序启动完毕");
    }
}
