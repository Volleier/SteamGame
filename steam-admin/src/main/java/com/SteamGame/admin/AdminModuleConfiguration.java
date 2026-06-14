package com.SteamGame.admin;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * steam-admin 模块配置 —— 由 steam-launcher 统一扫描启动。
 * admin 模块不需要自己的 @SpringBootApplication。
 */
@Configuration
@ComponentScan("com.SteamGame.admin")
public class AdminModuleConfiguration {
}
