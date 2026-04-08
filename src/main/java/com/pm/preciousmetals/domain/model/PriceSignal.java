package com.pm.preciousmetals.domain.model;

import java.util.Objects;

public record PriceSignal(Price price, MetalType metalType) {
    public PriceSignal {
        Objects.requireNonNull(price, "Price cannot be null");
        Objects.requireNonNull(metalType, "Metal type cannot be null");
    }
}
