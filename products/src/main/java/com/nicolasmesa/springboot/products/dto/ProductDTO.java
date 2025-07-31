package com.nicolasmesa.springboot.products.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ProductDTO {
    @NotNull
    @NotBlank(message = "Product name is required")
    @Size(max = 50, message = "Product name must be between 1 - 50 characters")
    private String name;

    @NotBlank(message = "Product description is required")
    @Size(max = 255, message = "Product description must be between 1 - 255 characters")
    private String description;

    @NotBlank(message = "Product category is required")
    @Size(max = 50, message = "Product category must be between 1 - 50 characters")
    private String category;

    @NotNull
    @Min(value = 0, message = "Price cannot be negative.")
    private double price;

    @NotBlank(message = "Currency is required")
    @Size(max = 50, message = "Currency must be 3 characters")
    private String currency;

    @NotNull
    @Min(value = 0, message = "Stock Available cannot be negative.")
    private int stockAvailable;

    @NotNull
    private boolean isAvailable;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStockAvailable() {
        return stockAvailable;
    }

    public void setStockAvailable(int stockAvailable) {
        this.stockAvailable = stockAvailable;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
