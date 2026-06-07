package com.example.mealplanner.dto;

import com.example.mealplanner.entity.Stock;

import java.time.LocalDateTime;

public class StockResponse {

    private Long id;
    private String name;
    private String category;
    private Boolean isAvailable;
    private LocalDateTime createdAt;

    public static StockResponse from(Stock s) {
        StockResponse res = new StockResponse();
        res.id = s.getId();
        res.name = s.getName();
        res.category = s.getCategory();
        res.isAvailable = s.getIsAvailable();
        res.createdAt = s.getCreatedAt();
        return res;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public Boolean getIsAvailable() { return isAvailable; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
