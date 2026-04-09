package com.pm.preciousmetals.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

public record PriceSignal(Price price, MetalType metalType) {
    private static final BigDecimal MAX_GOLD_PRICE = new BigDecimal("100000.00");
    private static final BigDecimal MAX_SILVER_PRICE = new BigDecimal("5000.00");

    public PriceSignal {
        Objects.requireNonNull(price, "Price cannot be null");
        Objects.requireNonNull(metalType, "Metal type cannot be null");

        if (metalType == MetalType.GOLD && price.value().compareTo(MAX_GOLD_PRICE) > 0) {
            throw new IllegalArgumentException("Price for gold exceeds maximum allowed value: " + MAX_GOLD_PRICE);
        }
        if (metalType == MetalType.SILVER && price.value().compareTo(MAX_SILVER_PRICE) > 0) {
            throw new IllegalArgumentException("Price for silver exceeds maximum allowed value: " + MAX_SILVER_PRICE);
        }
    }
}

