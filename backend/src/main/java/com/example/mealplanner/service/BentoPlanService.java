package com.example.mealplanner.service;

import com.example.mealplanner.dto.BentoPlanRandomRequest;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    // 月〜金の5日分をストックからランダム組み立て
    @Transactional
    public List<BentoPlanResponse> randomize(BentoPlanRandomRequest request) {
        LocalDate weekOf = request.getWeekOf();
        Random random = new Random();

        List<Stock> riceStocks  = stockRepository.findByIsAvailableAndCategory(true, "rice");
        List<Stock> mainStocks  = stockRepository.findByIsAvailableAndCategory(true, "main_dish");
        List<Stock> sideStocks  = stockRepository.findByIsAvailableAndCategory(true, "side_dish");
        List<Stock> noodleStocks = stockRepository.findByIsAvailableAndCategory(true, "noodle");

        List<BentoPlan> plans = new ArrayList<>();
        for (int i = 0; i < 5; i++) {  // 月(+0)〜金(+4)
            LocalDate date = weekOf.plusDays(i);
            BentoPlan bp = bentoPlanRepository.findByPlannedDate(date).orElse(new BentoPlan());
            bp.setPlannedDate(date);
            bp.setMemo(null);

            if (!noodleStocks.isEmpty() && random.nextBoolean()) {
                // 麺類の日（50%）
                bp.setNoodleStock(pick(noodleStocks, random));
                bp.setRiceStock(null);
                bp.setMainStock(null);
                bp.setSideStock(null);
            } else {
                bp.setNoodleStock(null);
                bp.setRiceStock(pick(riceStocks, random));
                bp.setMainStock(pick(mainStocks, random));
                bp.setSideStock(pick(sideStocks, random));
            }
            plans.add(bp);
        }

        return bentoPlanRepository.saveAll(plans).stream()
                .map(BentoPlanResponse::from)
                .toList();
    }

    private Stock pick(List<Stock> list, Random random) {
        if (list.isEmpty()) return null;
        return list.get(random.nextInt(list.size()));
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
