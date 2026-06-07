package com.example.mealplanner.dto;

import com.example.mealplanner.entity.BentoPlan;
import com.example.mealplanner.entity.Stock;

import java.time.LocalDate;

public class BentoPlanResponse {

    private Long id;
    private LocalDate plannedDate;
    private StockInfo rice;
    private StockInfo main;
    private StockInfo side;
    private StockInfo noodle;
    private String memo;

    public static BentoPlanResponse from(BentoPlan bp) {
        BentoPlanResponse res = new BentoPlanResponse();
        res.id = bp.getId();
        res.plannedDate = bp.getPlannedDate();
        res.rice = StockInfo.from(bp.getRiceStock());
        res.main = StockInfo.from(bp.getMainStock());
        res.side = StockInfo.from(bp.getSideStock());
        res.noodle = StockInfo.from(bp.getNoodleStock());
        res.memo = bp.getMemo();
        return res;
    }

    public static class StockInfo {
        private Long id;
        private String name;
        private String category;

        public static StockInfo from(Stock s) {
            if (s == null) return null;
            StockInfo info = new StockInfo();
            info.id = s.getId();
            info.name = s.getName();
            info.category = s.getCategory();
            return info;
        }

        public Long getId() { return id; }
        public String getName() { return name; }
        public String getCategory() { return category; }
    }

    public Long getId() { return id; }
    public LocalDate getPlannedDate() { return plannedDate; }
    public StockInfo getRice() { return rice; }
    public StockInfo getMain() { return main; }
    public StockInfo getSide() { return side; }
    public StockInfo getNoodle() { return noodle; }
    public String getMemo() { return memo; }
}
