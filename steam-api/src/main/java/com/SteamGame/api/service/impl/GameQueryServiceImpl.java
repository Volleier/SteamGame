package com.SteamGame.api.service.impl;

import com.SteamGame.api.dto.GameListItemDTO;
import com.SteamGame.api.dto.GameListPageDTO;
import com.SteamGame.api.dto.GameListQuery;
import com.SteamGame.api.mapper.OwnedGameMapper;
import com.SteamGame.api.service.GameQueryService;
import com.SteamGame.api.domain.OwnedGame;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameQueryServiceImpl implements GameQueryService {

    private final OwnedGameMapper ownedGameMapper;

    public GameQueryServiceImpl(OwnedGameMapper ownedGameMapper) {
        this.ownedGameMapper = ownedGameMapper;
    }

    @Override
    public GameListPageDTO queryGames(GameListQuery query) {
        String userId = query.getUserId() != null ? query.getUserId() : "default";
        List<OwnedGame> all = ownedGameMapper.listByUserId(userId);

        // filter by keyword
        if (query.getKeyword() != null && !query.getKeyword().isBlank()) {
            String kw = query.getKeyword().toLowerCase();
            all = all.stream()
                    .filter(g -> g.getName() != null && g.getName().toLowerCase().contains(kw))
                    .collect(Collectors.toList());
        }

        // sort
        String sort = query.getSort() != null ? query.getSort() : "name";
        boolean desc = "desc".equalsIgnoreCase(query.getOrder());
        java.util.Comparator<OwnedGame> comp = switch (sort) {
            case "playtime" -> java.util.Comparator.comparing(OwnedGame::getPlaytimeForever, java.util.Comparator.nullsLast(Integer::compareTo));
            case "releaseDate" -> java.util.Comparator.comparing(OwnedGame::getReleaseDate, java.util.Comparator.nullsLast(String::compareTo));
            default -> java.util.Comparator.comparing(OwnedGame::getName, java.util.Comparator.nullsLast(String::compareTo));
        };
        if (desc) comp = comp.reversed();
        all.sort(comp);

        // paginate
        int page = query.getPage();
        int pageSize = query.getPageSize();
        int total = all.size();
        int fromIndex = Math.min((page - 1) * pageSize, total);
        int toIndex = Math.min(fromIndex + pageSize, total);
        List<OwnedGame> pageList = all.subList(fromIndex, toIndex);

        List<GameListItemDTO> items = pageList.stream().map(g -> {
            GameListItemDTO dto = new GameListItemDTO();
            dto.setAppid(g.getAppid());
            dto.setName(g.getName());
            dto.setPlaytimeForever(g.getPlaytimeForever());
            dto.setDeveloper(g.getDeveloper());
            dto.setPublisher(g.getPublisher());
            dto.setReleaseDate(g.getReleaseDate());
            dto.setTags(g.getTags());
            dto.setHeaderImage(null); // populated later from metadata
            dto.setPlayerCount(null); // populated from realtime stats
            return dto;
        }).collect(Collectors.toList());

        GameListPageDTO result = new GameListPageDTO();
        result.setItems(items);
        result.setPage(page);
        result.setPageSize(pageSize);
        result.setTotal(total);
        return result;
    }
}
