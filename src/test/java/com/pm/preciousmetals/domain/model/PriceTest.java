package com.pm.preciousmetals.domain.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PriceTest {

    @Test
    void shouldCreateValidPrice() {
        assertThatCode(() -> new Price(new BigDecimal("100.00")))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldThrowExceptionWhenPriceIsNull() {
        assertThatThrownBy(() -> new Price(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Price value cannot be null");
    }

    @Test
    void shouldThrowExceptionWhenPriceIsZero() {
        assertThatThrownBy(() -> new Price(BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Price must be positive");
    }

    @Test
    void shouldThrowExceptionWhenPriceIsNegative() {
        assertThatThrownBy(() -> new Price(new BigDecimal("-1.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Price must be positive");
    }

    @Test
    void shouldThrowExceptionWhenPriceScaleIsTooLarge() {
        assertThatThrownBy(() -> new Price(new BigDecimal("100.12345")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Price scale cannot exceed 4");
    }

    @Test
    void shouldAcceptPriceWithMaxAllowedScale() {
        assertThatCode(() -> new Price(new BigDecimal("100.1234")))
                .doesNotThrowAnyException();
    }
}
