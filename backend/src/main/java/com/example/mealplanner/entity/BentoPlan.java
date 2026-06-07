package com.example.mealplanner.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bento_plans")
public class BentoPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate plannedDate;

    // 各欄1品のみ。麺類の日は noodleStock だけ入れて他は NULL
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rice_stock_id")
    private Stock riceStock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_stock_id")
    private Stock mainStock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "side_stock_id")
    private Stock sideStock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "noodle_stock_id")
    private Stock noodleStock;

    @Column(columnDefinition = "TEXT")
    private String memo;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public LocalDate getPlannedDate() { return plannedDate; }
    public void setPlannedDate(LocalDate plannedDate) { this.plannedDate = plannedDate; }
    public Stock getRiceStock() { return riceStock; }
    public void setRiceStock(Stock riceStock) { this.riceStock = riceStock; }
    public Stock getMainStock() { return mainStock; }
    public void setMainStock(Stock mainStock) { this.mainStock = mainStock; }
    public Stock getSideStock() { return sideStock; }
    public void setSideStock(Stock sideStock) { this.sideStock = sideStock; }
    public Stock getNoodleStock() { return noodleStock; }
    public void setNoodleStock(Stock noodleStock) { this.noodleStock = noodleStock; }
    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
