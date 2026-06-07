package com.example.mealplanner.controller;

import com.example.mealplanner.dto.StockRequest;
import com.example.mealplanner.dto.StockResponse;
import com.example.mealplanner.service.StockService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    public List<StockResponse> findAll(@RequestParam(defaultValue = "false") boolean all) {
        return stockService.findAll(all);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StockResponse create(@Valid @RequestBody StockRequest request) {
        return stockService.create(request);
    }

    @PutMapping("/{id}")
    public StockResponse update(@PathVariable Long id, @Valid @RequestBody StockRequest request) {
        return stockService.update(id, request);
    }

    @PatchMapping("/{id}/use")
    public StockResponse use(@PathVariable Long id) {
        return stockService.use(id);
    }

    @PatchMapping("/{id}/restore")
    public StockResponse restore(@PathVariable Long id) {
        return stockService.restore(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        stockService.delete(id);
    }
}
