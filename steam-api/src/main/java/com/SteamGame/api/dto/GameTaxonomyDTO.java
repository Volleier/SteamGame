package com.SteamGame.api.dto;

import java.util.List;

/**
 * DTO for taxonomy aggregation response (GET /api/game-taxonomy).
 */
public class GameTaxonomyDTO {
    private List<TaxonomyItemDTO> genres;
    private List<TaxonomyItemDTO> categories;
    private List<TaxonomyItemDTO> tags;

    public static class TaxonomyItemDTO {
        private Object id;
        private String name;
        private Integer count;
        private String source;

        public Object getId() { return id; }
        public void setId(Object id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getCount() { return count; }
        public void setCount(Integer count) { this.count = count; }
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
    }

    public List<TaxonomyItemDTO> getGenres() { return genres; }
    public void setGenres(List<TaxonomyItemDTO> genres) { this.genres = genres; }
    public List<TaxonomyItemDTO> getCategories() { return categories; }
    public void setCategories(List<TaxonomyItemDTO> categories) { this.categories = categories; }
    public List<TaxonomyItemDTO> getTags() { return tags; }
    public void setTags(List<TaxonomyItemDTO> tags) { this.tags = tags; }
}
