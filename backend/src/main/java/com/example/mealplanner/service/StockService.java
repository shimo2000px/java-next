package com.example.mealplanner.service;

import com.example.mealplanner.dto.StockRequest;
import com.example.mealplanner.dto.StockResponse;
import com.example.mealplanner.entity.Stock;
import com.example.mealplanner.exception.ResourceNotFoundException;
import com.example.mealplanner.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StockService {

    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    // all=true のとき全件、デフォルトは在庫ありのみ
    public List<StockResponse> findAll(boolean all) {
        List<Stock> stocks = all
                ? stockRepository.findAll()
                : stockRepository.findByIsAvailable(true);
        return stocks.stream().map(StockResponse::from).toList();
    }

    @Transactional
    public StockResponse create(StockRequest request) {
        Stock stock = new Stock();
        stock.setName(request.getName());
        stock.setCategory(request.getCategory());
        stock.setIsAvailable(true);
        return StockResponse.from(stockRepository.save(stock));
    }

    @Transactional
    public StockResponse update(Long id, StockRequest request) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ストックが見つかりません id=" + id));
        stock.setName(request.getName());
        stock.setCategory(request.getCategory());
        return StockResponse.from(stockRepository.save(stock));
    }

    @Transactional
    public StockResponse use(Long id) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ストックが見つかりません id=" + id));
        stock.setIsAvailable(false);
        return StockResponse.from(stockRepository.save(stock));
    }

    @Transactional
    public StockResponse restore(Long id) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ストックが見つかりません id=" + id));
        stock.setIsAvailable(true);
        return StockResponse.from(stockRepository.save(stock));
    }

    @Transactional
    public void delete(Long id) {
        if (!stockRepository.existsById(id)) {
            throw new ResourceNotFoundException("ストックが見つかりません id=" + id);
        }
        stockRepository.deleteById(id);
    }
}
