package com.SteamGame.api.dto;

import java.util.List;

/**
 * DTO for paged game list response (GET /api/games).
 */
public class GameListPageDTO {
    private List<GameListItemDTO> items;
    private int page;
    private int pageSize;
    private long total;

    public List<GameListItemDTO> getItems() { return items; }
    public void setItems(List<GameListItemDTO> items) { this.items = items; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
}
