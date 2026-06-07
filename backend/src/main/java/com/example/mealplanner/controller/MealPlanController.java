package com.example.mealplanner.controller;

import com.example.mealplanner.dto.MealPlanRandomRequest;
import com.example.mealplanner.dto.MealPlanResponse;
import com.example.mealplanner.dto.MealPlanUpdateRequest;
import com.example.mealplanner.service.MealPlanService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/meal-plans")
public class MealPlanController {

    private final MealPlanService mealPlanService;

    public MealPlanController(MealPlanService mealPlanService) {
        this.mealPlanService = mealPlanService;
    }

    @GetMapping
    public List<MealPlanResponse> findByWeek(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekOf) {
        return mealPlanService.findByWeek(weekOf);
    }

    @PostMapping("/random")
    @ResponseStatus(HttpStatus.CREATED)
    public List<MealPlanResponse> generateRandom(@Valid @RequestBody MealPlanRandomRequest request) {
        return mealPlanService.generateRandom(request);
    }

    @PutMapping("/{id}")
    public MealPlanResponse update(@PathVariable Long id, @Valid @RequestBody MealPlanUpdateRequest request) {
        return mealPlanService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        mealPlanService.delete(id);
    }
}
