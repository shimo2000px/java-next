package com.example.mealplanner.dto;

import com.example.mealplanner.entity.MealPlan;
import com.example.mealplanner.entity.Recipe;

import java.time.LocalDate;

public class MealPlanResponse {

    private Long id;
    private LocalDate plannedDate;
    private Boolean isRandom;
    private RecipeInfo recipe;

    public static MealPlanResponse from(MealPlan mp) {
        MealPlanResponse res = new MealPlanResponse();
        res.id = mp.getId();
        res.plannedDate = mp.getPlannedDate();
        res.isRandom = mp.getIsRandom();
        res.recipe = mp.getRecipe() != null ? RecipeInfo.from(mp.getRecipe()) : null;
        return res;
    }

    public static class RecipeInfo {
        private Long id;
        private String name;
        private String category;

        public static RecipeInfo from(Recipe r) {
            RecipeInfo info = new RecipeInfo();
            info.id = r.getId();
            info.name = r.getName();
            info.category = r.getCategory();
            return info;
        }

        public Long getId() { return id; }
        public String getName() { return name; }
        public String getCategory() { return category; }
    }

    public Long getId() { return id; }
    public LocalDate getPlannedDate() { return plannedDate; }
    public Boolean getIsRandom() { return isRandom; }
    public RecipeInfo getRecipe() { return recipe; }
}
