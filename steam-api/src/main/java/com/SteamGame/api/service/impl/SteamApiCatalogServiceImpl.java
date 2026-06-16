package com.SteamGame.api.service.impl;

import com.SteamGame.api.client.steam.SteamWebApiClient;
import com.SteamGame.api.dto.SteamApiSupportedListDTO;
import com.SteamGame.api.service.SteamApiCatalogService;
import com.SteamGame.common.context.CredentialProvider;
import com.SteamGame.common.context.SteamCredential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SteamApiCatalogServiceImpl implements SteamApiCatalogService {

    private static final Logger log = LoggerFactory.getLogger(SteamApiCatalogServiceImpl.class);

    private final SteamWebApiClient webApiClient;
    private final CredentialProvider credentialProvider;

    public SteamApiCatalogServiceImpl(SteamWebApiClient webApiClient, CredentialProvider credentialProvider) {
        this.webApiClient = webApiClient;
        this.credentialProvider = credentialProvider;
    }

    @Override
    public SteamApiSupportedListDTO getSupportedApiList(boolean withKey) {
        try {
            String apiKey = null;
            if (withKey) {
                SteamCredential cred = credentialProvider.getCurrentCredential("default");
                if (cred != null && cred.isValid()) {
                    apiKey = cred.getApiKey();
                }
            }
            return webApiClient.getSupportedApiList(apiKey);
        } catch (Exception e) {
            log.error("Failed to fetch supported API list: {}", e.getMessage());
            SteamApiSupportedListDTO dto = new SteamApiSupportedListDTO();
            dto.setSource("ISteamWebAPIUtil/GetSupportedAPIList");
            dto.setWithKey(withKey);
            dto.setInterfaces(java.util.List.of());
            return dto;
        }
    }
}
