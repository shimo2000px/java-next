package com.example.mealplanner.repository;

import com.example.mealplanner.entity.BentoPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BentoPlanRepository extends JpaRepository<BentoPlan, Long> {
    List<BentoPlan> findByPlannedDateBetweenOrderByPlannedDate(LocalDate start, LocalDate end);
    Optional<BentoPlan> findByPlannedDate(LocalDate plannedDate);
}
