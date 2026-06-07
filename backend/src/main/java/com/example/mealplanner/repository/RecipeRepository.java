package com.example.mealplanner.repository;

import com.example.mealplanner.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findByUserId(Long userId);
    List<Recipe> findByCategory(String category);
}
