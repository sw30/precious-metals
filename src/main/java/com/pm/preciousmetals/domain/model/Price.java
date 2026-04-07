package com.pm.preciousmetals.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

public record Price(BigDecimal value) {
    private static final int MAX_SCALE = 4;

    public Price {
        Objects.requireNonNull(value, "Price value cannot be null");
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        if (value.scale() > MAX_SCALE) {
            throw new IllegalArgumentException("Price scale cannot exceed " + MAX_SCALE);
        }
    }

    public static Price of(BigDecimal value) {
        return new Price(value);
    }
}
