package com.pm.preciousmetals.domain.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PriceSignalTest {

    @Test
    void shouldCreateValidPriceSignal() {
        Price price = new Price(new BigDecimal("2500.00"));
        assertThatCode(() -> new PriceSignal(price, MetalType.GOLD))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldThrowExceptionWhenPriceExceedsMaxForGold() {
        Price highPrice = new Price(new BigDecimal("100000.01"));
        assertThatThrownBy(() -> new PriceSignal(highPrice, MetalType.GOLD))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Price for gold exceeds maximum allowed value");
    }

    @Test
    void shouldThrowExceptionWhenPriceExceedsMaxForSilver() {
        Price highPrice = new Price(new BigDecimal("5000.01"));
        assertThatThrownBy(() -> new PriceSignal(highPrice, MetalType.SILVER))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Price for silver exceeds maximum allowed value");
    }

    @Test
    void shouldAcceptMaxAllowedPrice() {
        Price maxPrice = new Price(new BigDecimal("5000.00"));
        assertThatCode(() -> new PriceSignal(maxPrice, MetalType.SILVER))
                .doesNotThrowAnyException();
    }
}
