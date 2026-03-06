package com.myproject.models.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
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
 * DTO for Cart Response
 * Maps to CartResponse schema in OpenAPI specification
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("userId")
    private UUID userId;

    @JsonProperty("items")
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();

    @JsonProperty("totalPrice")
    private BigDecimal totalPrice;

    @JsonProperty("updatedAt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime updatedAt;

    @JsonProperty("isEmpty")
    private Boolean isEmpty;

    @JsonProperty("emptyMessage")
    private String emptyMessage;
}