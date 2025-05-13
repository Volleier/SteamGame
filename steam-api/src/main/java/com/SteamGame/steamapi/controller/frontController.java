package com.SteamGame.steamapi.controller;

import com.SteamGame.steamapi.dbOperate.dbSearch;
import com.SteamGame.steamapi.pogo.GameList;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/front")
@CrossOrigin(origins = "http://localhost:8081")
public class frontController {

    @GetMapping("/games")
    public List<GameList> getGames() {
        return dbSearch.searchGames();
    }

    @GetMapping("/hello")
    public String SayHello() {
        return "Hello World From SpringBoot!";
    }
}