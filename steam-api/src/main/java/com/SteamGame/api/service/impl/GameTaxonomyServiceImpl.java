package com.SteamGame.api.service.impl;

import com.SteamGame.api.dto.GameTaxonomyDTO;
import com.SteamGame.api.service.GameTaxonomyService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class GameTaxonomyServiceImpl implements GameTaxonomyService {

    // In a full implementation this would aggregate from game_genre, game_category, and game_metadata tables.
    // For the MVP, it returns an empty structure so the frontend can render the taxonomy panel.
    @Override
    public GameTaxonomyDTO getTaxonomy() {
        GameTaxonomyDTO dto = new GameTaxonomyDTO();
        dto.setGenres(new ArrayList<>());
        dto.setCategories(new ArrayList<>());
        dto.setTags(new ArrayList<>());
        return dto;
    }
}
