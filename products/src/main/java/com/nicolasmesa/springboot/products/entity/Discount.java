package com.nicolasmesa.springboot.products.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "tb_discounts")
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false, length = 50)
    private String discountCode;

    @Column(nullable = false, length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    private DiscountTypes discountType;

    @Column(nullable = false)
    private double discount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false)
    private double minimumPurchaseAmount;

    @Column(nullable = false)
    private boolean isActive;

    @Column(nullable = false)
    private int timesUsed;

    @Column
    private int maxUses;

    @OneToMany(mappedBy = "discount")
    private List<DiscountedProduct> discountedProducts = new ArrayList<>();

    @Column(nullable = false)
    private boolean isStackable;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime lastUsedAt;

    @Column(nullable = false, length = 50)
    private String updatedBy;

    @Column(updatable = false, nullable = false, length = 50)
    private String createdBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        timesUsed = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Discount(String discountCode, String description, DiscountTypes discountType, double discount, String currency, double minimumPurchaseAmount, boolean isActive, int timesUsed, int maxUses, boolean isStackable, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.discountCode = discountCode;
        this.description = description;
        this.discountType = discountType;
        this.discount = discount;
        this.currency = currency;
        this.minimumPurchaseAmount = minimumPurchaseAmount;
        this.isActive = isActive;
        this.timesUsed = timesUsed;
        this.maxUses = maxUses;
        this.isStackable = isStackable;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DiscountTypes getDiscountType() {
        return discountType;
    }

    public void setDiscountType(DiscountTypes discountType) {
        this.discountType = discountType;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getMinimumPurchaseAmount() {
        return minimumPurchaseAmount;
    }

    public void setMinimumPurchaseAmount(double minimumPurchaseAmount) {
        this.minimumPurchaseAmount = minimumPurchaseAmount;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getTimesUsed() {
        return timesUsed;
    }

    public void setTimesUsed(int timesUsed) {
        this.timesUsed = timesUsed;
    }

    public int getMaxUses() {
        return maxUses;
    }

    public void setMaxUses(int maxUses) {
        this.maxUses = maxUses;
    }

    public List<DiscountedProduct> getDiscountedProducts() {
        return discountedProducts;
    }

    public void setDiscountedProducts(List<DiscountedProduct> discountedProducts) {
        this.discountedProducts = discountedProducts;
    }

    public boolean isStackable() {
        return isStackable;
    }

    public void setStackable(boolean stackable) {
        isStackable = stackable;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLastUsedAt() {
        return lastUsedAt;
    }

    public void setLastUsedAt(LocalDateTime lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getCreatedBy() {
        return createdBy;
    }
}
