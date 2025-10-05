package com.nicolasmesa.springboot.productservices.discounts.entity;

import com.nicolasmesa.springboot.common.model.Currency;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "tb_discounts")
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false, length = 20)
    private String discountCode;

    @Column(nullable = false, length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountTypes discountType;

    @Column(nullable = false)
    private Double discount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private Currency currency;

    @Column(nullable = false)
    private Double minimumPurchaseAmount;

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false)
    private Integer timesUsed;

    @Column
    private Integer maxUses;

    @OneToMany(mappedBy = "discount")
    private List<DiscountedProduct> discountedProducts = new ArrayList<>();

    @Column(nullable = false)
    private Boolean isStackable;

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
        updatedAt = LocalDateTime.now();
        createdAt = LocalDateTime.now();
        timesUsed = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
