package com.SteamGame.login.service;

import com.SteamGame.common.context.SteamCredential;
import com.SteamGame.login.service.impl.CredentialProviderImpl;
import com.SteamGame.util.CryptoUtil;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CredentialProviderImplTest {

    @Test
    void readsAndDecryptsCredentialFromYaml() throws Exception {
        Path authFile = Files.createTempFile("steam-auth-", ".yaml");
        String base64Key = Base64.getEncoder().encodeToString("12345678901234567890123456789012".getBytes());
        CryptoUtil.EncryptResult encrypted = CryptoUtil.encrypt("TEST_API_KEY", base64Key);
        Files.writeString(authFile, """
                security:
                  credentialKey:
                    base64Key: %s
                auth:
                  steamId: "76561198000000000"
                  apiKeyEncrypted: "%s"
                  keyMeta:
                    iv: "%s"
                """.formatted(base64Key, encrypted.cipherTextBase64, encrypted.ivBase64));

        CredentialKeyService keyService = mock(CredentialKeyService.class);
        when(keyService.loadKey()).thenReturn(base64Key);

        CredentialProviderImpl provider = new CredentialProviderImpl();
        ReflectionTestUtils.setField(provider, "configPath", authFile.toString());
        ReflectionTestUtils.setField(provider, "keyService", keyService);
        ReflectionTestUtils.setField(provider, "sessionStore", null);

        SteamCredential credential = provider.getCurrentCredential("user-1");

        assertTrue(credential.isValid());
        assertEquals("user-1", credential.getUserId());
        assertEquals("76561198000000000", credential.getSteamId());
        assertEquals("TEST_API_KEY", credential.getApiKey());
    }

    @Test
    void returnsInvalidCredentialWhenYamlMissingKey() throws Exception {
        Path authFile = Files.createTempFile("steam-auth-missing-key-", ".yaml");
        Files.writeString(authFile, """
                auth:
                  steamId: "76561198000000000"
                """);

        CredentialKeyService keyService = mock(CredentialKeyService.class);
        when(keyService.loadKey()).thenReturn(null);

        CredentialProviderImpl provider = new CredentialProviderImpl();
        ReflectionTestUtils.setField(provider, "configPath", authFile.toString());
        ReflectionTestUtils.setField(provider, "keyService", keyService);
        ReflectionTestUtils.setField(provider, "sessionStore", null);

        SteamCredential credential = provider.getCurrentCredential(null);

        assertFalse(credential.isValid());
        assertEquals("default", credential.getUserId());
    }
}
