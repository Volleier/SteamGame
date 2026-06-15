package com.SteamGame.common.paging;

/**
 * Unified pagination request model.
 */
public class PageRequest {
    private int page = 1;
    private int pageSize = 20;

    public PageRequest() {}

    public PageRequest(int page, int pageSize) {
        this.page = Math.max(1, page);
        this.pageSize = Math.min(100, Math.max(1, pageSize));
    }

    public int getPage() { return page; }

    public void setPage(int page) { this.page = Math.max(1, page); }

    public int getPageSize() { return pageSize; }

    public void setPageSize(int pageSize) { this.pageSize = Math.min(100, Math.max(1, pageSize)); }

    public int getOffset() { return (page - 1) * pageSize; }
}
