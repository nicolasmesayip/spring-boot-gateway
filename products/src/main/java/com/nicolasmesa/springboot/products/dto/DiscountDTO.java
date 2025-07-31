package com.nicolasmesa.springboot.products.dto;

import com.nicolasmesa.springboot.products.entity.DiscountTypes;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class DiscountDTO {

    @NotBlank(message = "Discount code is required")
    @Size(max = 50, message = "Discount code must be between 1 - 50 characters")
    private String discountCode;

    @NotBlank(message = "Description is required")
    @Size(max = 255, message = "Description must be between 1 - 255 characters")
    private String description;

    @NotNull
    private DiscountTypes discountType;

    @NotNull
    private double discount;

    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be 3 characters")
    private String currency;

    @NotNull
    private double minimumPurchaseAmount;

    @NotNull
    private boolean isActive;

    @NotNull
    private int timesUsed;

    @NotNull
    private int maxUses;

    @NotNull
    private boolean isStackable;

    @NotNull
    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    public String getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
