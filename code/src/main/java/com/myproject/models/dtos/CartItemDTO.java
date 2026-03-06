package com.myproject.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO representing a cart item
 * Maps to CartItem schema in OpenAPI
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {

    @NotNull(message = "Cart item ID cannot be null")
    @JsonProperty("id")
    private UUID id;

    @NotNull(message = "Product ID cannot be null")
    @JsonProperty("productId")
    private UUID productId;

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    @JsonProperty("quantity")
    private Integer quantity;

    @NotNull(message = "Price cannot be null")
    @Min(value = 0, message = "Price must be non-negative")
    @JsonProperty("price")
    private BigDecimal price;

    /**
     * Calculates the total price for this cart item
     * @return total price (quantity * price)
     */
    public BigDecimal calculateItemTotal() {
        if (price == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
