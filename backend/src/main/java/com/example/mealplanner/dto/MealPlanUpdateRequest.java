package com.example.mealplanner.dto;

import jakarta.validation.constraints.NotNull;

public class MealPlanUpdateRequest {

    @NotNull(message = "recipeId は必須です")
    private Long recipeId;

    public Long getRecipeId() { return recipeId; }
    public void setRecipeId(Long recipeId) { this.recipeId = recipeId; }
}
