package com.nicolasmesa.springboot.products.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "tb_discounted_products")
public class DiscountedProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "discount_id", nullable = false)
    private Discount discount;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
