package com.pm.preciousmetals.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.Objects;

public record PriceSignalRequest(
        @NotNull(message = "Price is mandatory")
        @Positive(message = "Price must be positive")
        BigDecimal price,

        @NotBlank(message = "Metal type cannot be empty")
        @JsonProperty("itemType")
        String itemType
) {
    public PriceSignalRequest {
        Objects.requireNonNull(price, "Price is mandatory");
        Objects.requireNonNull(itemType, "Item type is mandatory");
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        if (itemType.isBlank()) {
            throw new IllegalArgumentException("Metal type cannot be empty");
        }
    }
}
