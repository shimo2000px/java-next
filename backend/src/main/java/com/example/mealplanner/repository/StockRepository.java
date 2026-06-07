package com.example.mealplanner.repository;

import com.example.mealplanner.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockRepository extends JpaRepository<Stock, Long> {
    List<Stock> findByIsAvailable(boolean isAvailable);
    List<Stock> findByIsAvailableAndCategory(boolean isAvailable, String category);
}
