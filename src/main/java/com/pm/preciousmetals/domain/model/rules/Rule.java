package com.pm.preciousmetals.domain.model.rules;

import com.pm.preciousmetals.domain.model.PriceSignal;
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

    public boolean matches(PriceSignal signal) {
        if (operand == Operand.PRICE) {
            BigDecimal signalValue = signal.price().value();
            int comparison = signalValue.compareTo(targetValue);
            return switch (operator) {
                case IS_EQUAL -> comparison == 0;
                case IS_NOT_EQUAL -> comparison != 0;
                case GREATER_THAN -> comparison > 0;
                case GREATER_THAN_EQUAL -> comparison >= 0;
                case LESS_THAN -> comparison < 0;
                case LESS_THAN_EQUAL -> comparison <= 0;
            };
        } else if (operand == Operand.ITEM) {
            int metalOrdinal = signal.metalType().ordinal();
            int targetOrdinal = targetValue.intValue();
            return switch (operator) {
                case IS_EQUAL -> metalOrdinal == targetOrdinal;
                case IS_NOT_EQUAL -> metalOrdinal != targetOrdinal;
                default -> false;
            };
        }
        return false;
    }
}
