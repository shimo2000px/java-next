package com.example.mealplanner.controller;

import com.example.mealplanner.dto.BentoPlanRandomRequest;
import com.example.mealplanner.dto.BentoPlanRequest;
import com.example.mealplanner.dto.BentoPlanResponse;
import com.example.mealplanner.service.BentoPlanService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bento-plans")
public class BentoPlanController {

    private final BentoPlanService bentoPlanService;

    public BentoPlanController(BentoPlanService bentoPlanService) {
        this.bentoPlanService = bentoPlanService;
    }

    @GetMapping
    public List<BentoPlanResponse> findByWeek(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekOf) {
        return bentoPlanService.findByWeek(weekOf);
    }

    @PostMapping("/random")
    @ResponseStatus(HttpStatus.CREATED)
    public List<BentoPlanResponse> randomize(@Valid @RequestBody BentoPlanRandomRequest request) {
        return bentoPlanService.randomize(request);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BentoPlanResponse save(@Valid @RequestBody BentoPlanRequest request) {
        return bentoPlanService.save(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        bentoPlanService.delete(id);
    }
}
