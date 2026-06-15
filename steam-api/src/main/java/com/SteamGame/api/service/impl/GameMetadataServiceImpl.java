package com.SteamGame.api.service.impl;

import com.SteamGame.api.client.store.SteamStoreClient;
import com.SteamGame.api.domain.metadata.GameCategory;
import com.SteamGame.api.domain.metadata.GameGenre;
import com.SteamGame.api.domain.metadata.GameMetadata;
import com.SteamGame.api.dto.metadata.GameMetadataDTO;
import com.SteamGame.api.dto.metadata.GameMetadataSyncResultDTO;
import com.SteamGame.api.mapper.GameCategoryMapper;
import com.SteamGame.api.mapper.GameGenreMapper;
import com.SteamGame.api.mapper.GameMetadataMapper;
import com.SteamGame.api.service.GameMetadataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class GameMetadataServiceImpl implements GameMetadataService {

    private static final Logger log = LoggerFactory.getLogger(GameMetadataServiceImpl.class);

    private final SteamStoreClient storeClient;
    private final GameMetadataMapper metadataMapper;
    private final GameCategoryMapper categoryMapper;
    private final GameGenreMapper genreMapper;

    public GameMetadataServiceImpl(SteamStoreClient storeClient,
                                    GameMetadataMapper metadataMapper,
                                    GameCategoryMapper categoryMapper,
                                    GameGenreMapper genreMapper) {
        this.storeClient = storeClient;
        this.metadataMapper = metadataMapper;
        this.categoryMapper = categoryMapper;
        this.genreMapper = genreMapper;
    }

    @Override
    public GameMetadataDTO getMetadata(Long appid) {
        GameMetadata cached = metadataMapper.findByAppid(appid);
        if (cached != null) {
            return toDTO(cached);
        }
        // fetch from Store
        return syncMetadata(appid, null, null);
    }

    @Override
    public GameMetadataDTO syncMetadata(Long appid, String language, String countryCode) {
        try {
            GameMetadataDTO dto = storeClient.getAppDetails(appid, language, countryCode);
            if (dto != null) {
                saveMetadata(dto);
            }
            return dto;
        } catch (Exception e) {
            log.error("Failed to sync metadata for appid={}: {}", appid, e.getMessage());
            return null;
        }
    }

    @Override
    public GameMetadataSyncResultDTO syncMissing(String userId, int limit) {
        List<Long> appids = metadataMapper.findAppidsMissingMetadata(userId, limit);
        GameMetadataSyncResultDTO result = new GameMetadataSyncResultDTO();
        result.setRequested(appids.size());
        int updated = 0;
        int failed = 0;
        for (int i = 0; i < appids.size(); i++) {
            if (i > 0) {
                try { Thread.sleep(1500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); break; }
            }
            try {
                GameMetadataDTO dto = storeClient.getAppDetails(appids.get(i), null, null);
                if (dto != null) {
                    saveMetadata(dto);
                    updated++;
                } else {
                    failed++;
                }
            } catch (Exception e) {
                log.warn("Failed to sync metadata for appid={}: {}", appids.get(i), e.getMessage());
                failed++;
            }
        }
        result.setUpdated(updated);
        result.setFailed(failed);
        return result;
    }

    private void saveMetadata(GameMetadataDTO dto) {
        GameMetadata meta = new GameMetadata();
        meta.setAppid(dto.getAppid());
        meta.setName(dto.getName());
        meta.setType(dto.getType());
        meta.setShortDescription(dto.getShortDescription());
        meta.setHeaderImage(dto.getHeaderImage());
        meta.setCapsuleImage(dto.getCapsuleImage());
        meta.setWebsite(dto.getWebsite());
        meta.setDevelopers(dto.getDevelopers() != null ? String.join(",", dto.getDevelopers()) : null);
        meta.setPublishers(dto.getPublishers() != null ? String.join(",", dto.getPublishers()) : null);
        meta.setReleaseDate(dto.getReleaseDate());
        meta.setComingSoon(dto.getComingSoon());
        if (dto.getPlatforms() != null) {
            meta.setPlatformWindows(dto.getPlatforms().getWindows());
            meta.setPlatformMac(dto.getPlatforms().getMac());
            meta.setPlatformLinux(dto.getPlatforms().getLinux());
        }
        if (dto.getPrice() != null) {
            meta.setPriceCurrency(dto.getPrice().getCurrency());
            meta.setPriceInitial(dto.getPrice().getInitial());
            meta.setPriceFinal(dto.getPrice().getFin());
            meta.setDiscountPercent(dto.getPrice().getDiscountPercent());
        }
        meta.setMetadataSource(dto.getMetadataSource());
        Timestamp now = new Timestamp(System.currentTimeMillis());
        meta.setMetadataSyncedAt(now);
        meta.setCreatedAt(now);
        meta.setUpdatedAt(now);
        metadataMapper.upsert(meta);

        // save categories
        if (dto.getCategories() != null) {
            categoryMapper.deleteByAppid(dto.getAppid());
            for (var cat : dto.getCategories()) {
                GameCategory gc = new GameCategory();
                gc.setAppid(dto.getAppid());
                gc.setCategoryId(cat.getId());
                gc.setDescription(cat.getDescription());
                gc.setSource("store_appdetails");
                gc.setUpdatedAt(now);
                categoryMapper.upsert(gc);
            }
        }

        // save genres
        if (dto.getGenres() != null) {
            genreMapper.deleteByAppid(dto.getAppid());
            for (var gen : dto.getGenres()) {
                GameGenre gg = new GameGenre();
                gg.setAppid(dto.getAppid());
                gg.setGenreId(gen.getId());
                gg.setDescription(gen.getDescription());
                gg.setSource("store_appdetails");
                gg.setUpdatedAt(now);
                genreMapper.upsert(gg);
            }
        }
    }

    private GameMetadataDTO toDTO(GameMetadata m) {
        GameMetadataDTO dto = new GameMetadataDTO();
        dto.setAppid(m.getAppid());
        dto.setName(m.getName());
        dto.setType(m.getType());
        dto.setShortDescription(m.getShortDescription());
        dto.setHeaderImage(m.getHeaderImage());
        dto.setCapsuleImage(m.getCapsuleImage());
        dto.setWebsite(m.getWebsite());
        dto.setDevelopers(m.getDevelopers() != null ? List.of(m.getDevelopers().split(",")) : null);
        dto.setPublishers(m.getPublishers() != null ? List.of(m.getPublishers().split(",")) : null);
        dto.setReleaseDate(m.getReleaseDate());
        dto.setComingSoon(m.getComingSoon());
        GameMetadataDTO.PlatformsDTO plats = new GameMetadataDTO.PlatformsDTO();
        plats.setWindows(m.getPlatformWindows());
        plats.setMac(m.getPlatformMac());
        plats.setLinux(m.getPlatformLinux());
        dto.setPlatforms(plats);
        if (m.getPriceCurrency() != null) {
            GameMetadataDTO.PriceDTO price = new GameMetadataDTO.PriceDTO();
            price.setCurrency(m.getPriceCurrency());
            price.setInitial(m.getPriceInitial());
            price.setFin(m.getPriceFinal());
            price.setDiscountPercent(m.getDiscountPercent());
            dto.setPrice(price);
        }
        // categories and genres loaded lazily from their tables if needed
        dto.setMetadataSource(m.getMetadataSource());
        dto.setMetadataSyncedAt(m.getMetadataSyncedAt());
        return dto;
    }
}
