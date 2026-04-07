package com.pm.preciousmetals.domain;

import java.math.BigDecimal;
import java.util.Objects;

public record Price(BigDecimal value) {
    public Price {
        Objects.requireNonNull(value, "Price value cannot be null");
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
    }

    public static Price of(BigDecimal value) {
        return new Price(value);
    }
}
