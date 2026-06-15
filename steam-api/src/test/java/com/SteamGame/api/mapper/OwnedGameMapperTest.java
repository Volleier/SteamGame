package com.SteamGame.api.mapper;

import com.SteamGame.api.domain.OwnedGame;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@MybatisTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:owned_game_mapper_test;DATABASE_TO_UPPER=false;MODE=MySQL",
        "spring.sql.init.mode=always"
})
class OwnedGameMapperTest {

    @SpringBootApplication
    @ComponentScan("com.SteamGame.api.mapper")
    static class TestApplication {
    }

    @Autowired
    private OwnedGameMapper mapper;

    @Test
    void upsertSameUserAndAppDoesNotCreateDuplicateRows() {
        OwnedGame first = game("user-a", 730L, "Counter-Strike 2", 10);
        OwnedGame updated = game("user-a", 730L, "Counter-Strike 2", 25);

        mapper.upsert(first);
        mapper.upsert(updated);

        List<OwnedGame> games = mapper.listByUserId("user-a");
        assertEquals(1, games.size());
        assertEquals(25, games.get(0).getPlaytimeForever());
        assertEquals(1, mapper.countByUserId("user-a"));
    }

    @Test
    void updateDetailsIsScopedByUserIdAndAppid() {
        mapper.upsert(game("user-a", 730L, "Counter-Strike 2", 10));
        mapper.upsert(game("user-b", 730L, "Counter-Strike 2", 10));

        mapper.updateDetails("user-a", 730L, "Valve", "Valve", "2012-08-21", "FPS,Multiplayer");

        OwnedGame userA = mapper.listByUserId("user-a").get(0);
        OwnedGame userB = mapper.listByUserId("user-b").get(0);

        assertEquals("Valve", userA.getDeveloper());
        assertEquals("Valve", userA.getPublisher());
        assertEquals("2012-08-21", userA.getReleaseDate());
        assertEquals("FPS,Multiplayer", userA.getTags());
        assertNull(userB.getDeveloper());
        assertNull(userB.getPublisher());
    }

    @Test
    void listMissingDetailsOnlyReturnsRequestedUserAndLimit() {
        mapper.upsert(game("user-a", 1L, "A", 1));
        mapper.upsert(game("user-a", 2L, "B", 1));
        mapper.upsert(game("user-b", 3L, "C", 1));

        List<OwnedGame> missing = mapper.listMissingDetailsByUserId("user-a", 1);

        assertEquals(1, missing.size());
        assertEquals("user-a", missing.get(0).getUserId());
    }

    private OwnedGame game(String userId, Long appid, String name, int playtimeForever) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        OwnedGame game = new OwnedGame();
        game.setUserId(userId);
        game.setSteamId("76561198000000000");
        game.setAppid(appid);
        game.setName(name);
        game.setPlaytimeForever(playtimeForever);
        game.setLastSyncedAt(now);
        game.setUpdatedAt(now);
        return game;
    }
}
