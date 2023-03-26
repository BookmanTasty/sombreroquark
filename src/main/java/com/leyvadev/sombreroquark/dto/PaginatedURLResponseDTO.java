package com.leyvadev.sombreroquark.dto;

import com.leyvadev.sombreroquark.model.SombreroAllowedRedirectUrl;
import com.leyvadev.sombreroquark.model.SombreroUser;

import java.util.List;

public class PaginatedURLResponseDTO {
    private List<SombreroAllowedRedirectUrl> urls;
    private int currentPage;
    private int pageSize;
    private long totalItems;

    public List<SombreroAllowedRedirectUrl> getUrls() {
        return urls;
    }

    public void setUrls(List<SombreroAllowedRedirectUrl> urls) {
        this.urls = urls;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(long totalItems) {
        this.totalItems = totalItems;
    }
}