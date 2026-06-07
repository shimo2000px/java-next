package com.example.mealplanner.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class BentoPlanRequest {

    @NotNull(message = "plannedDate は必須です")
    private LocalDate plannedDate;

    private Long riceStockId;
    private Long mainStockId;
    private Long sideStockId;
    private Long noodleStockId;
    private String memo;

    public LocalDate getPlannedDate() { return plannedDate; }
    public void setPlannedDate(LocalDate plannedDate) { this.plannedDate = plannedDate; }
    public Long getRiceStockId() { return riceStockId; }
    public void setRiceStockId(Long riceStockId) { this.riceStockId = riceStockId; }
    public Long getMainStockId() { return mainStockId; }
    public void setMainStockId(Long mainStockId) { this.mainStockId = mainStockId; }
    public Long getSideStockId() { return sideStockId; }
    public void setSideStockId(Long sideStockId) { this.sideStockId = sideStockId; }
    public Long getNoodleStockId() { return noodleStockId; }
    public void setNoodleStockId(Long noodleStockId) { this.noodleStockId = noodleStockId; }
    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }
}
