package com.pm.preciousmetals.domain.model;

import java.math.BigDecimal;
import java.util.stream.Stream;

public enum MetalType {
    GOLD("gold", new BigDecimal("100000")),
    SILVER("silver", new BigDecimal("5000")),
    PLATINUM("platinum", new BigDecimal("100000"));

    private final String value;
    private final BigDecimal maxPrice;

    MetalType(String value, BigDecimal maxPrice) {
        this.value = value;
        this.maxPrice = maxPrice;
    }

    public String getValue() {
        return value;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public static MetalType fromValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Metal type cannot be empty");
        }
        return Stream.of(MetalType.values())
                .filter(type -> type.value.equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid metal type: " + value + ". Allowed: gold, silver, platinum"));
    }
}
