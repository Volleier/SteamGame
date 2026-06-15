package com.SteamGame.api.dto;

/**
 * Query parameters for game list endpoint (GET /api/games).
 */
public class GameListQuery {
    private String userId;
    private String keyword;
    private String genre;
    private String category;
    private String sort;
    private String order;
    private Integer page;
    private Integer pageSize;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getSort() { return sort; }
    public void setSort(String sort) { this.sort = sort; }
    public String getOrder() { return order; }
    public void setOrder(String order) { this.order = order; }
    public Integer getPage() { return page != null ? page : 1; }
    public void setPage(Integer page) { this.page = page; }
    public Integer getPageSize() { return pageSize != null ? pageSize : 20; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
}
