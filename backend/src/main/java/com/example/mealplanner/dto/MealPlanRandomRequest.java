package com.example.mealplanner.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class MealPlanRandomRequest {

    @NotNull(message = "weekOf は必須です（月曜日の日付）")
    private LocalDate weekOf;

    public LocalDate getWeekOf() { return weekOf; }
    public void setWeekOf(LocalDate weekOf) { this.weekOf = weekOf; }
}
