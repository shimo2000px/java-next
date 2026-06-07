package com.example.mealplanner.dto;

import com.example.mealplanner.entity.Recipe;

import java.time.LocalDateTime;

public class RecipeResponse {

    private Long id;
    private String name;
    private String category;
    private String size;
    private String memo;
    private LocalDateTime createdAt;

    public static RecipeResponse from(Recipe recipe) {
        RecipeResponse res = new RecipeResponse();
        res.id = recipe.getId();
        res.name = recipe.getName();
        res.category = recipe.getCategory();
        res.size = recipe.getSize();
        res.memo = recipe.getMemo();
        res.createdAt = recipe.getCreatedAt();
        return res;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getSize() { return size; }
    public String getMemo() { return memo; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
