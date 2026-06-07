package com.example.mealplanner.service;

import com.example.mealplanner.dto.BentoPlanRequest;
import com.example.mealplanner.dto.BentoPlanResponse;
import com.example.mealplanner.entity.BentoPlan;
import com.example.mealplanner.entity.Stock;
import com.example.mealplanner.exception.ResourceNotFoundException;
import com.example.mealplanner.repository.BentoPlanRepository;
import com.example.mealplanner.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class BentoPlanService {

    private final BentoPlanRepository bentoPlanRepository;
    private final StockRepository stockRepository;

    public BentoPlanService(BentoPlanRepository bentoPlanRepository, StockRepository stockRepository) {
        this.bentoPlanRepository = bentoPlanRepository;
        this.stockRepository = stockRepository;
    }

    public List<BentoPlanResponse> findByWeek(LocalDate weekOf) {
        return bentoPlanRepository
                .findByPlannedDateBetweenOrderByPlannedDate(weekOf, weekOf.plusDays(6))
                .stream()
                .map(BentoPlanResponse::from)
                .toList();
    }

    // 同じ plannedDate が既にあれば上書き、なければ新規作成
    @Transactional
    public BentoPlanResponse save(BentoPlanRequest request) {
        BentoPlan bp = bentoPlanRepository
                .findByPlannedDate(request.getPlannedDate())
                .orElse(new BentoPlan());

        bp.setPlannedDate(request.getPlannedDate());
        bp.setRiceStock(resolveStock(request.getRiceStockId()));
        bp.setMainStock(resolveStock(request.getMainStockId()));
        bp.setSideStock(resolveStock(request.getSideStockId()));
        bp.setNoodleStock(resolveStock(request.getNoodleStockId()));
        bp.setMemo(request.getMemo());

        return BentoPlanResponse.from(bentoPlanRepository.save(bp));
    }

    @Transactional
    public void delete(Long id) {
        if (!bentoPlanRepository.existsById(id)) {
            throw new ResourceNotFoundException("お弁当計画が見つかりません id=" + id);
        }
        bentoPlanRepository.deleteById(id);
    }

    private Stock resolveStock(Long stockId) {
        if (stockId == null) return null;
        return stockRepository.findById(stockId)
                .orElseThrow(() -> new ResourceNotFoundException("ストックが見つかりません id=" + stockId));
    }
}
