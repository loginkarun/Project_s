package com.myproject.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing a shopping cart
 */
@Entity
@Table(name = "cart", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<CartItemEntity> items = new ArrayList<>();

    @Column(name = "total_price", nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Helper method to add item to cart
     */
    public void addItem(CartItemEntity item) {
        items.add(item);
        item.setCart(this);
        recalculateTotalPrice();
    }

    /**
     * Helper method to remove item from cart
     */
    public void removeItem(CartItemEntity item) {
        items.remove(item);
        item.setCart(null);
        recalculateTotalPrice();
    }

    /**
     * Recalculate total price based on items
     */
    public void recalculateTotalPrice() {
        this.totalPrice = items.stream()
            .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Check if cart is empty
     */
    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }
}