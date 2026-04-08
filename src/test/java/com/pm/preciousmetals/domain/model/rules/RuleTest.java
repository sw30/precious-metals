package com.pm.preciousmetals.domain.model.rules;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RuleTest {

    @Test
    void shouldCreateValidRuleForPrice() {
        assertThatCode(() -> new Rule(Operand.PRICE, Operator.GREATER_THAN, new BigDecimal("100")))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldThrowExceptionWhenFieldIsNull() {
        assertThatThrownBy(() -> new Rule(null, Operator.IS_EQUAL, BigDecimal.ONE))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Operand is mandatory");
    }

    @Test
    void shouldThrowExceptionWhenOperatorIsNull() {
        // Ten test obecnie prawdopodobnie zawiedzie lub rzuci NPE z błędnym komunikatem (bo field jest sprawdzany dwa razy)
        assertThatThrownBy(() -> new Rule(Operand.ITEM, null, BigDecimal.ONE))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Operator is mandatory");
    }

    @Test
    void shouldThrowExceptionWhenTargetValueIsNull() {
        assertThatThrownBy(() -> new Rule(Operand.ITEM, Operator.IS_EQUAL, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Target value is mandatory");
    }

    @Test
    void shouldThrowExceptionWhenItemHasMathematicalOperator() {
        assertThatThrownBy(() -> new Rule(Operand.ITEM, Operator.GREATER_THAN, new BigDecimal("10")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Field ITEM only supports IS_EQUAL and IS_NOT_EQUAL operators");
    }

    @Test
    void shouldAllowPriceWithAllOperators() {
        for (Operator op : Operator.values()) {
            assertThatCode(() -> new Rule(Operand.PRICE, op, new BigDecimal("100")))
                    .as("Operator %s should be allowed for PRICE", op)
                    .doesNotThrowAnyException();
        }
    }
}
