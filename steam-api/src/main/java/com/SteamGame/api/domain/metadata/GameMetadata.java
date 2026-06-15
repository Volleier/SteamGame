package com.SteamGame.api.domain.metadata;

import java.sql.Timestamp;

/**
 * Domain entity for game_metadata table.
 * Stores Steam Store AppDetails data keyed by appid.
 */
public class GameMetadata {
    private Long appid;
    private String name;
    private String type;
    private String shortDescription;
    private String detailedDescription;
    private String headerImage;
    private String capsuleImage;
    private String website;
    private String developers;
    private String publishers;
    private String releaseDate;
    private Boolean comingSoon;
    private Boolean platformWindows;
    private Boolean platformMac;
    private Boolean platformLinux;
    private String priceCurrency;
    private Integer priceInitial;
    private Integer priceFinal;
    private Integer discountPercent;
    private Integer metacriticScore;
    private String pcRequirements;
    private String metadataSource;
    private Timestamp metadataSyncedAt;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Long getAppid() { return appid; }
    public void setAppid(Long appid) { this.appid = appid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getShortDescription() { return shortDescription; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }

    public String getDetailedDescription() { return detailedDescription; }
    public void setDetailedDescription(String detailedDescription) { this.detailedDescription = detailedDescription; }

    public String getHeaderImage() { return headerImage; }
    public void setHeaderImage(String headerImage) { this.headerImage = headerImage; }

    public String getCapsuleImage() { return capsuleImage; }
    public void setCapsuleImage(String capsuleImage) { this.capsuleImage = capsuleImage; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getDevelopers() { return developers; }
    public void setDevelopers(String developers) { this.developers = developers; }

    public String getPublishers() { return publishers; }
    public void setPublishers(String publishers) { this.publishers = publishers; }

    public String getReleaseDate() { return releaseDate; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }

    public Boolean getComingSoon() { return comingSoon; }
    public void setComingSoon(Boolean comingSoon) { this.comingSoon = comingSoon; }

    public Boolean getPlatformWindows() { return platformWindows; }
    public void setPlatformWindows(Boolean platformWindows) { this.platformWindows = platformWindows; }

    public Boolean getPlatformMac() { return platformMac; }
    public void setPlatformMac(Boolean platformMac) { this.platformMac = platformMac; }

    public Boolean getPlatformLinux() { return platformLinux; }
    public void setPlatformLinux(Boolean platformLinux) { this.platformLinux = platformLinux; }

    public String getPriceCurrency() { return priceCurrency; }
    public void setPriceCurrency(String priceCurrency) { this.priceCurrency = priceCurrency; }

    public Integer getPriceInitial() { return priceInitial; }
    public void setPriceInitial(Integer priceInitial) { this.priceInitial = priceInitial; }

    public Integer getPriceFinal() { return priceFinal; }
    public void setPriceFinal(Integer priceFinal) { this.priceFinal = priceFinal; }

    public Integer getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(Integer discountPercent) { this.discountPercent = discountPercent; }

    public Integer getMetacriticScore() { return metacriticScore; }
    public void setMetacriticScore(Integer metacriticScore) { this.metacriticScore = metacriticScore; }

    public String getPcRequirements() { return pcRequirements; }
    public void setPcRequirements(String pcRequirements) { this.pcRequirements = pcRequirements; }

    public String getMetadataSource() { return metadataSource; }
    public void setMetadataSource(String metadataSource) { this.metadataSource = metadataSource; }

    public Timestamp getMetadataSyncedAt() { return metadataSyncedAt; }
    public void setMetadataSyncedAt(Timestamp metadataSyncedAt) { this.metadataSyncedAt = metadataSyncedAt; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
