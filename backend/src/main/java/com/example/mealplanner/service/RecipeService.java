package com.example.mealplanner.service;

import com.example.mealplanner.dto.RecipeRequest;
import com.example.mealplanner.dto.RecipeResponse;
import com.example.mealplanner.entity.Recipe;
import com.example.mealplanner.exception.ResourceNotFoundException;
import com.example.mealplanner.repository.RecipeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    // Phase 2 でログインユーザー絞り込みに変更予定
    public List<RecipeResponse> findAll() {
        return recipeRepository.findAll().stream()
                .map(RecipeResponse::from)
                .toList();
    }

    @Transactional
    public RecipeResponse create(RecipeRequest request) {
        Recipe recipe = new Recipe();
        recipe.setName(request.getName());
        recipe.setCategory(request.getCategory());
        recipe.setSize(request.getSize());
        recipe.setMemo(request.getMemo());
        return RecipeResponse.from(recipeRepository.save(recipe));
    }

    @Transactional
    public RecipeResponse update(Long id, RecipeRequest request) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("レシピが見つかりません id=" + id));
        recipe.setName(request.getName());
        recipe.setCategory(request.getCategory());
        recipe.setSize(request.getSize());
        recipe.setMemo(request.getMemo());
        return RecipeResponse.from(recipeRepository.save(recipe));
    }

    @Transactional
    public void delete(Long id) {
        if (!recipeRepository.existsById(id)) {
            throw new ResourceNotFoundException("レシピが見つかりません id=" + id);
        }
        recipeRepository.deleteById(id);
    }
}
