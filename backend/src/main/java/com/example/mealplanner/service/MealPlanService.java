package com.example.mealplanner.service;

import com.example.mealplanner.dto.MealPlanResponse;
import com.example.mealplanner.dto.MealPlanRandomRequest;
import com.example.mealplanner.dto.MealPlanUpdateRequest;
import com.example.mealplanner.entity.MealPlan;
import com.example.mealplanner.entity.Recipe;
import com.example.mealplanner.exception.ResourceNotFoundException;
import com.example.mealplanner.repository.MealPlanRepository;
import com.example.mealplanner.repository.RecipeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class MealPlanService {

    private final MealPlanRepository mealPlanRepository;
    private final RecipeRepository recipeRepository;

    public MealPlanService(MealPlanRepository mealPlanRepository, RecipeRepository recipeRepository) {
        this.mealPlanRepository = mealPlanRepository;
        this.recipeRepository = recipeRepository;
    }

    public List<MealPlanResponse> findByWeek(LocalDate weekOf) {
        return mealPlanRepository
                .findByPlannedDateBetweenOrderByPlannedDate(weekOf, weekOf.plusDays(6))
                .stream()
                .map(MealPlanResponse::from)
                .toList();
    }

    @Transactional
    public List<MealPlanResponse> generateRandom(MealPlanRandomRequest request) {
        LocalDate weekOf = request.getWeekOf();
        List<LocalDate> week = IntStream.range(0, 7)
                .mapToObj(weekOf::plusDays)
                .toList();

        List<Recipe> all = recipeRepository.findAll();
        if (all.isEmpty()) {
            throw new IllegalArgumentException("レシピが登録されていません。先にレシピを登録してください。");
        }

        // シャッフルしてコピー（元のリストを変更しない）
        List<Recipe> shuffled = new ArrayList<>(all);
        Collections.shuffle(shuffled);

        // 7日分に割り振り（レシピ数 < 7 の場合はループ）
        List<Recipe> assigned = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            assigned.add(shuffled.get(i % shuffled.size()));
        }

        // 連続する同一レシピを次のものと入れ替え
        for (int i = 1; i < assigned.size(); i++) {
            if (assigned.get(i).getId().equals(assigned.get(i - 1).getId())) {
                int next = (i + 1) % assigned.size();
                Collections.swap(assigned, i, next);
            }
        }

        // 既存データを削除してから一括INSERT
        mealPlanRepository.deleteByPlannedDateBetween(weekOf, weekOf.plusDays(6));

        List<MealPlan> plans = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            MealPlan mp = new MealPlan();
            mp.setPlannedDate(week.get(i));
            mp.setRecipe(assigned.get(i));
            mp.setIsRandom(true);
            plans.add(mp);
        }

        return mealPlanRepository.saveAll(plans).stream()
                .map(MealPlanResponse::from)
                .toList();
    }

    @Transactional
    public MealPlanResponse update(Long id, MealPlanUpdateRequest request) {
        MealPlan mp = mealPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("献立が見つかりません id=" + id));
        Recipe recipe = recipeRepository.findById(request.getRecipeId())
                .orElseThrow(() -> new ResourceNotFoundException("レシピが見つかりません id=" + request.getRecipeId()));
        mp.setRecipe(recipe);
        mp.setIsRandom(false);
        return MealPlanResponse.from(mealPlanRepository.save(mp));
    }

    @Transactional
    public void delete(Long id) {
        if (!mealPlanRepository.existsById(id)) {
            throw new ResourceNotFoundException("献立が見つかりません id=" + id);
        }
        mealPlanRepository.deleteById(id);
    }
}
