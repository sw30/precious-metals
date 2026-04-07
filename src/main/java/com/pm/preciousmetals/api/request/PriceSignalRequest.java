package com.pm.preciousmetals.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pm.preciousmetals.domain.MetalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.Objects;

public record PriceSignalRequest(
        @NotNull(message = "Price is mandatory")
        @Positive(message = "Price must be positive")
        BigDecimal price,

        @NotNull(message = "Item type is mandatory")
        @JsonProperty("itemType")
        MetalType itemType
) {
    public PriceSignalRequest {
        Objects.requireNonNull(price, "Price is mandatory");
        Objects.requireNonNull(itemType, "Item type is mandatory");
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
    }
}
