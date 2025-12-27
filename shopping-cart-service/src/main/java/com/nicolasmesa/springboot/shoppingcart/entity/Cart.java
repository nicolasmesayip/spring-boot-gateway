package com.nicolasmesa.springboot.shoppingcart.entity;

import com.nicolasmesa.springboot.common.model.Currency;
import com.nicolasmesa.springboot.shoppingcart.enums.CartStatus;
import com.nicolasmesa.springboot.shoppingcart.exception.CartItemNotFound;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_carts")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CartStatus status = CartStatus.ACTIVE;

    @Column(nullable = false, unique = true, length = 100, updatable = false)
    private String userId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private Currency currency = Currency.GBP;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void addItem(CartItem item) {
        items.add(item);
        item.setCart(this);
    }

    public void removeItem(CartItem item) {
        items.removeIf(itemP -> itemP.getProductSlug().equals(item.getProductSlug()));
    }

    public void updateItem(CartItem item) {
        CartItem tempItem = findItemByProductSlug(item).orElseThrow(() -> new CartItemNotFound(item.getProductSlug()));

        tempItem.setUnitPrice(item.getUnitPrice());
        tempItem.setQuantity(item.getQuantity());
    }

    public Optional<CartItem> findItemByProductSlug(CartItem cartItem) {
        return getItems().stream().filter(item -> item.getProductSlug().equals(cartItem.getProductSlug())).findFirst();
    }
}
