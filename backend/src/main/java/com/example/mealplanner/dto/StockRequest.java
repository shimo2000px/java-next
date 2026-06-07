package com.example.mealplanner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class StockRequest {

    @NotBlank(message = "名前は必須です")
    private String name;

    @NotBlank(message = "カテゴリは必須です")
    @Pattern(regexp = "^(rice|main_dish|side_dish|noodle)$",
             message = "category は rice/main_dish/side_dish/noodle のいずれか")
    private String category;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
