package com.myproject.models.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DTO representing the shopping cart response
 * Maps to CartResponse schema in OpenAPI
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponseDTO {

    @NotNull(message = "Cart ID cannot be null")
    @JsonProperty("id")
    private UUID id;

    @NotNull(message = "User ID cannot be null")
    @JsonProperty("userId")
    private UUID userId;

    @NotNull(message = "Items list cannot be null")
    @JsonProperty("items")
    @Builder.Default
    private List<CartItemDTO> items = new ArrayList<>();

    @NotNull(message = "Total price cannot be null")
    @JsonProperty("totalPrice")
    @Builder.Default
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @NotNull(message = "Updated timestamp cannot be null")
    @JsonProperty("updatedAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime updatedAt;

    @JsonProperty("isEmpty")
    private Boolean isEmpty;

    @JsonProperty("emptyMessage")
    private String emptyMessage;

    /**
     * Checks if cart is empty and sets appropriate fields
     */
    public void checkAndSetEmptyStatus() {
        this.isEmpty = (items == null || items.isEmpty());
        if (Boolean.TRUE.equals(this.isEmpty)) {
            this.emptyMessage = "Your cart is empty";
            this.totalPrice = BigDecimal.ZERO;
        } else {
            this.emptyMessage = null;
        }
    }
}
