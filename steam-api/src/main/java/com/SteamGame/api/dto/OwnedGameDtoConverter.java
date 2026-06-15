package com.SteamGame.api.dto;

import com.SteamGame.api.domain.OwnedGame;

import java.util.List;
import java.util.stream.Collectors;

public class OwnedGameDtoConverter {

    public OwnedGameDTO toDto(OwnedGame game) {
        if (game == null) {
            return null;
        }
        OwnedGameDTO dto = new OwnedGameDTO();
        dto.setAppid(game.getAppid());
        dto.setName(game.getName());
        dto.setPlaytimeForever(game.getPlaytimeForever());
        dto.setDeveloper(game.getDeveloper());
        dto.setPublisher(game.getPublisher());
        dto.setReleaseDate(game.getReleaseDate());
        dto.setTags(game.getTags());
        return dto;
    }

    public List<OwnedGameDTO> toDtoList(List<OwnedGame> games) {
        if (games == null) {
            return List.of();
        }
        return games.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
