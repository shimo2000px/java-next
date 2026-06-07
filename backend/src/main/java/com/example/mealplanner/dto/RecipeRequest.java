package com.example.mealplanner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class RecipeRequest {

    @NotBlank(message = "レシピ名は必須です")
    private String name;

    @NotBlank(message = "カテゴリは必須です")
    @Pattern(regexp = "^(main_dish|rice|noodle)$", message = "category は main_dish/rice/noodle のいずれか")
    private String category;

    private String size; // large / medium / small（任意）
    private String memo; // 任意

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }
}
