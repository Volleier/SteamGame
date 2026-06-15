package com.SteamGame.launcher;

import com.SteamGame.api.service.OwnedGameService;
import com.SteamGame.common.context.CredentialProvider;
import com.SteamGame.api.controller.OwnedGamesController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SteamLauncherApplicationTests {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
        assertNotNull(context);
    }

    @Test
    void ownedGameServiceBeanExists() {
        assertNotNull(context.getBean(OwnedGameService.class));
    }

    @Test
    void credentialProviderBeanExists() {
        assertNotNull(context.getBean(CredentialProvider.class));
    }

    @Test
    void ownedGamesControllerBeanExists() {
        assertNotNull(context.getBean(OwnedGamesController.class));
    }
}
