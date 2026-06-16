package com.SteamGame.api.dto.metadata;

import java.util.List;
import java.sql.Timestamp;

/**
 * DTO for game metadata detail response (GET /api/game-metadata/{appid}).
 */
public class GameMetadataDTO {
    private Long appid;
    private String name;
    private String type;
    private String shortDescription;
    private String headerImage;
    private String capsuleImage;
    private String website;
    private List<String> developers;
    private List<String> publishers;
    private String releaseDate;
    private Boolean comingSoon;
    private PlatformsDTO platforms;
    private PriceDTO price;
    private List<CategoryDTO> categories;
    private List<GenreDTO> genres;
    private List<ScreenshotDTO> screenshots;
    private String metadataSource;
    private Timestamp metadataSyncedAt;

    public static class PlatformsDTO {
        private Boolean windows;
        private Boolean mac;
        private Boolean linux;
        public Boolean getWindows() { return windows; }
        public void setWindows(Boolean windows) { this.windows = windows; }
        public Boolean getMac() { return mac; }
        public void setMac(Boolean mac) { this.mac = mac; }
        public Boolean getLinux() { return linux; }
        public void setLinux(Boolean linux) { this.linux = linux; }
    }

    public static class PriceDTO {
        private String currency;
        private Integer initial;
        private Integer fin;
        private Integer discountPercent;
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
        public Integer getInitial() { return initial; }
        public void setInitial(Integer initial) { this.initial = initial; }
        public Integer getFin() { return fin; }
        public void setFin(Integer fin) { this.fin = fin; }
        public Integer getDiscountPercent() { return discountPercent; }
        public void setDiscountPercent(Integer discountPercent) { this.discountPercent = discountPercent; }
    }

    public static class CategoryDTO {
        private Integer id;
        private String description;
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class GenreDTO {
        private String id;
        private String description;
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class ScreenshotDTO {
        private Integer id;
        private String pathThumbnail;
        private String pathFull;
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
        public String getPathThumbnail() { return pathThumbnail; }
        public void setPathThumbnail(String pathThumbnail) { this.pathThumbnail = pathThumbnail; }
        public String getPathFull() { return pathFull; }
        public void setPathFull(String pathFull) { this.pathFull = pathFull; }
    }

    public Long getAppid() { return appid; }
    public void setAppid(Long appid) { this.appid = appid; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getShortDescription() { return shortDescription; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }
    public String getHeaderImage() { return headerImage; }
    public void setHeaderImage(String headerImage) { this.headerImage = headerImage; }
    public String getCapsuleImage() { return capsuleImage; }
    public void setCapsuleImage(String capsuleImage) { this.capsuleImage = capsuleImage; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    public List<String> getDevelopers() { return developers; }
    public void setDevelopers(List<String> developers) { this.developers = developers; }
    public List<String> getPublishers() { return publishers; }
    public void setPublishers(List<String> publishers) { this.publishers = publishers; }
    public String getReleaseDate() { return releaseDate; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }
    public Boolean getComingSoon() { return comingSoon; }
    public void setComingSoon(Boolean comingSoon) { this.comingSoon = comingSoon; }
    public PlatformsDTO getPlatforms() { return platforms; }
    public void setPlatforms(PlatformsDTO platforms) { this.platforms = platforms; }
    public PriceDTO getPrice() { return price; }
    public void setPrice(PriceDTO price) { this.price = price; }
    public List<CategoryDTO> getCategories() { return categories; }
    public void setCategories(List<CategoryDTO> categories) { this.categories = categories; }
    public List<GenreDTO> getGenres() { return genres; }
    public void setGenres(List<GenreDTO> genres) { this.genres = genres; }
    public List<ScreenshotDTO> getScreenshots() { return screenshots; }
    public void setScreenshots(List<ScreenshotDTO> screenshots) { this.screenshots = screenshots; }
    public String getMetadataSource() { return metadataSource; }
    public void setMetadataSource(String metadataSource) { this.metadataSource = metadataSource; }
    public Timestamp getMetadataSyncedAt() { return metadataSyncedAt; }
    public void setMetadataSyncedAt(Timestamp metadataSyncedAt) { this.metadataSyncedAt = metadataSyncedAt; }
}
