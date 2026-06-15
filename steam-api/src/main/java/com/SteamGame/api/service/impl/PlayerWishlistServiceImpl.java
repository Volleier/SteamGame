package com.SteamGame.api.service.impl;

import com.SteamGame.api.client.steam.SteamWebApiClient;
import com.SteamGame.api.domain.player.PlayerWishlist;
import com.SteamGame.api.dto.player.WishlistItemDTO;
import com.SteamGame.api.dto.player.WishlistResultDTO;
import com.SteamGame.api.mapper.PlayerWishlistMapper;
import com.SteamGame.api.service.PlayerWishlistService;
import com.SteamGame.api.service.GameMetadataService;
import com.SteamGame.common.context.CredentialProvider;
import com.SteamGame.common.context.SteamCredential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlayerWishlistServiceImpl implements PlayerWishlistService {

    private static final Logger log = LoggerFactory.getLogger(PlayerWishlistServiceImpl.class);

    private final CredentialProvider credentialProvider;
    private final SteamWebApiClient webApiClient;
    private final PlayerWishlistMapper wishlistMapper;
    private final GameMetadataService gameMetadataService;

    public PlayerWishlistServiceImpl(CredentialProvider credentialProvider,
                                     SteamWebApiClient webApiClient,
                                     PlayerWishlistMapper wishlistMapper,
                                     GameMetadataService gameMetadataService) {
        this.credentialProvider = credentialProvider;
        this.webApiClient = webApiClient;
        this.wishlistMapper = wishlistMapper;
        this.gameMetadataService = gameMetadataService;
    }

    @Override
    public WishlistResultDTO getWishlist(String userId) {
        try {
            SteamCredential cred = credentialProvider.getCurrentCredential(userId);
            if (cred != null && cred.isValid()) {
                List<WishlistItemDTO> items = webApiClient.getWishlist(cred.getSteamId(), cred.getApiKey());
                if (items != null) {
                    for (WishlistItemDTO item : items) {
                        try {
                            var meta = gameMetadataService.getMetadata(item.getAppid());
                            if (meta != null && meta.getName() != null) {
                                item.setName(meta.getName());
                            }
                        } catch (Exception e) {
                            log.warn("Failed to fetch metadata for wishlist item appid={}: {}", item.getAppid(), e.getMessage());
                        }
                    }
                    saveWishlist(userId, items);
                    return wrap(items);
                }
            }
        } catch (Exception e) {
            log.warn("Wishlist unavailable for userId={}: {}", userId, e.getMessage());
        }

        List<WishlistItemDTO> cached = wishlistMapper.findByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return wrap(cached);
    }

    private void saveWishlist(String userId, List<WishlistItemDTO> items) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        for (WishlistItemDTO dto : items) {
            PlayerWishlist item = new PlayerWishlist();
            item.setUserId(userId);
            item.setAppid(dto.getAppid());
            item.setName(dto.getName());
            item.setPriority(dto.getPriority());
            item.setAddedAt(dto.getAddedAt());
            item.setSyncedAt(now);
            wishlistMapper.upsert(item);
        }
    }

    private WishlistItemDTO toDto(PlayerWishlist item) {
        WishlistItemDTO dto = new WishlistItemDTO();
        dto.setAppid(item.getAppid());
        String name = item.getName();
        if (name == null || name.isEmpty()) {
            try {
                var meta = gameMetadataService.getMetadata(item.getAppid());
                if (meta != null) {
                    name = meta.getName();
                }
            } catch (Exception e) {
                // ignore
            }
        }
        dto.setName(name);
        dto.setPriority(item.getPriority());
        dto.setAddedAt(item.getAddedAt());
        return dto;
    }

    private WishlistResultDTO wrap(List<WishlistItemDTO> items) {
        WishlistResultDTO result = new WishlistResultDTO();
        result.setItems(items);
        return result;
    }
}
