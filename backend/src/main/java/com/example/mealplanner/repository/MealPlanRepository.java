package com.example.mealplanner.repository;

import com.example.mealplanner.entity.MealPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MealPlanRepository extends JpaRepository<MealPlan, Long> {
    List<MealPlan> findByPlannedDateBetweenOrderByPlannedDate(LocalDate start, LocalDate end);
    void deleteByPlannedDateBetween(LocalDate start, LocalDate end);
}
