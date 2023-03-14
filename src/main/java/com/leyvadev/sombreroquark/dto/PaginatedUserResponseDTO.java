package com.leyvadev.sombreroquark.dto;

import com.leyvadev.sombreroquark.model.SombreroUser;

import java.util.List;

public class PaginatedUserResponseDTO {
    private List<SombreroUser> users;
    private int currentPage;
    private int pageSize;
    private long totalItems;

    public List<SombreroUser> getUsers() {
        return users;
    }

    public void setUsers(List<SombreroUser> users) {
        this.users = users;
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