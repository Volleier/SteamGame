package com.SteamGame.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * Shared HTTP client configuration for Steam API and Store API calls.
 * All requests must set timeouts, never log full URLs containing keys,
 * and return explicit exceptions on non-200 responses.
 */
@Configuration
public class SteamHttpClientConfig {

    @Value("${steam.api.timeoutSeconds:15}")
    private int timeoutSeconds;

    @Value("${steam.api.detailsTimeoutSeconds:6}")
    private int detailsTimeoutSeconds;

    @Value("${steam.api.detailsDelayMillis:1500}")
    private long detailsDelayMillis;

    @Value("${steam.api.maxBatchSize:50}")
    private int maxBatchSize;

    @Value("${steam.api.storeLanguage:zh-cn}")
    private String storeLanguage;

    @Value("${steam.api.storeCountryCode:CN}")
    private String storeCountryCode;

    @Bean
    public HttpClient steamHttpClient() {
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public int getTimeoutSeconds() { return timeoutSeconds; }
    public int getDetailsTimeoutSeconds() { return detailsTimeoutSeconds; }
    public long getDetailsDelayMillis() { return detailsDelayMillis; }
    public int getMaxBatchSize() { return maxBatchSize; }
    public String getStoreLanguage() { return storeLanguage; }
    public String getStoreCountryCode() { return storeCountryCode; }
}
