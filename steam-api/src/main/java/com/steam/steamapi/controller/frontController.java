package com.steam.steamapi.controller;

import com.steam.steamapi.dbOperate.dbSearch;
import com.steam.steamapi.pogo.GameList;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:8080")
public class frontController {

    @GetMapping("/games")
    public List<GameList> getGames() {
        return dbSearch.searchGames();
    }
}