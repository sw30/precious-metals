package com.pm.preciousmetals.domain.model.rules;

import com.pm.preciousmetals.domain.model.MetalType;
import com.pm.preciousmetals.domain.model.Price;
import com.pm.preciousmetals.domain.model.PriceSignal;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RuleTest {

    @Test
    void shouldCreateValidRuleForPrice() {
        assertThatCode(() -> new PriceRule(Operator.GREATER_THAN, new BigDecimal("100")))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldThrowExceptionWhenOperatorIsNullForPrice() {
        assertThatThrownBy(() -> new PriceRule(null, BigDecimal.ONE))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Operator is mandatory");
    }

    @Test
    void shouldThrowExceptionWhenTargetValueIsNullForPrice() {
        assertThatThrownBy(() -> new PriceRule(Operator.IS_EQUAL, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Target value is mandatory");
    }

    @Test
    void shouldThrowExceptionWhenItemHasMathematicalOperator() {
        assertThatThrownBy(() -> new ItemRule(Operator.GREATER_THAN, MetalType.GOLD))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Field ITEM only supports IS_EQUAL and IS_NOT_EQUAL operators");
    }

    @Test
    void shouldThrowExceptionWhenMetalTypeIsNullForItem() {
        assertThatThrownBy(() -> new ItemRule(Operator.IS_EQUAL, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Metal type is mandatory");
    }

    @Test
    void shouldAllowPriceWithAllOperators() {
        for (Operator op : Operator.values()) {
            assertThatCode(() -> new PriceRule(op, new BigDecimal("100")))
                    .as("Operator %s should be allowed for PRICE", op)
                    .doesNotThrowAnyException();
        }
    }

    @Test
    void itemRuleShouldMatchCorrectMetal() {

        ItemRule goldRule = new ItemRule(Operator.IS_EQUAL, MetalType.GOLD);
        ItemRule notSilverRule = new ItemRule(Operator.IS_NOT_EQUAL, MetalType.SILVER);

        Price dummyPrice = new Price(BigDecimal.TEN);
        PriceSignal goldSignal = new PriceSignal(dummyPrice, MetalType.GOLD);
        PriceSignal silverSignal = new PriceSignal(dummyPrice, MetalType.SILVER);

        assertThat(goldRule.matches(goldSignal)).isTrue();
        assertThat(goldRule.matches(silverSignal)).isFalse();
        assertThat(notSilverRule.matches(goldSignal)).isTrue();
        assertThat(notSilverRule.matches(silverSignal)).isFalse();
    }
}

