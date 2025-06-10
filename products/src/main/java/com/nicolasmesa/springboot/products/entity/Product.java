package com.nicolasmesa.springboot.products.entity;

import jakarta.persistence.*;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "tb_product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Size(min = 1, max = 50, message = "Category must be between 1 - 50 characters")
    private String name;

    @NotNull
    @Size(min = 1, max = 255, message = "Category must be between 1 - 255 characters")
    private String description;

    @NotNull
    @Size(min = 1, max = 50, message = "Category must be between 1 - 50 characters")
    private String category;

    @NotNull
    @Min(value = 0, message = "Price cannot be negative.")
    private double price;

    @NotNull
    @Min(value = 0, message = "Stock Available cannot be negative.")
    private int stockAvailable;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    public Product() {
    }

    public Product(String name, String description, String category, double price, int stockAvailable) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.stockAvailable = stockAvailable;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
