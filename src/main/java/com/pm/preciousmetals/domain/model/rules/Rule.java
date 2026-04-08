package com.pm.preciousmetals.domain.model.rules;

import java.math.BigDecimal;
import java.util.Objects;

public record Rule(Operand operand, Operator operator, BigDecimal targetValue) {
    public Rule {
        Objects.requireNonNull(operand, "Operand is mandatory");
        Objects.requireNonNull(operator, "Operator is mandatory");
        Objects.requireNonNull(targetValue, "Target value is mandatory");

        if (Operand.ITEM == operand && (Operator.IS_EQUAL != operator && Operator.IS_NOT_EQUAL != operator)) {
            throw new IllegalArgumentException("Field ITEM only supports IS_EQUAL and IS_NOT_EQUAL operators");
        }
    }
}
