package com.pm.preciousmetals.domain.model;

import java.util.stream.Stream;

public enum MetalType {
    GOLD("gold"),
    SILVER("silver"),
    PLATINUM("platinum");

    private final String value;

    MetalType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
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

