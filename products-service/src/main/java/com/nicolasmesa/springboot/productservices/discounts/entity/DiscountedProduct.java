package com.nicolasmesa.springboot.productservices.discounts.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "tb_discounted_product")
@NoArgsConstructor
public class DiscountedProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "discount_id", nullable = false)
    private Discount discount;

    @Column(name = "product_slug", nullable = false)
    private String productSlug;

    public DiscountedProduct(Discount discount, String productSlug) {
        this.discount = discount;
        this.productSlug = productSlug;
    }
}
