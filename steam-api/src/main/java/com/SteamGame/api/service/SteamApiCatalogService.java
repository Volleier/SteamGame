package com.SteamGame.api.service;

import com.SteamGame.api.dto.SteamApiSupportedListDTO;

public interface SteamApiCatalogService {
    SteamApiSupportedListDTO getSupportedApiList(boolean withKey);
}
