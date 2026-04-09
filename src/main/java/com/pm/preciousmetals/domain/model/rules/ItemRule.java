package com.pm.preciousmetals.domain.model.rules;

import com.pm.preciousmetals.domain.model.MetalType;
import com.pm.preciousmetals.domain.model.PriceSignal;
import java.util.Objects;

public record ItemRule(Operator operator, MetalType metalType) implements Rule {
    public ItemRule {
        Objects.requireNonNull(operator, "Operator is mandatory");
        Objects.requireNonNull(metalType, "Metal type is mandatory");

        if (Operator.IS_EQUAL != operator && Operator.IS_NOT_EQUAL != operator) {
            throw new IllegalArgumentException("Field ITEM only supports IS_EQUAL and IS_NOT_EQUAL operators");
        }
    }

    @Override
    public boolean matches(PriceSignal signal) {
        MetalType signalMetal = signal.metalType();
        return switch (operator) {
            case IS_EQUAL -> signalMetal == metalType;
            case IS_NOT_EQUAL -> signalMetal != metalType;
            default -> false;
        };
    }

    @Override
    public boolean isExclusive(Rule other) {
        if (other instanceof ItemRule(Operator operator1, MetalType type)) {
            if (this.operator == Operator.IS_EQUAL && operator1 == Operator.IS_EQUAL) {
                return this.metalType != type;
            }
            if (this.operator == Operator.IS_EQUAL && operator1 == Operator.IS_NOT_EQUAL) {
                return this.metalType == type;
            }
            if (this.operator == Operator.IS_NOT_EQUAL && operator1 == Operator.IS_EQUAL) {
                return this.metalType == type;
            }
        }
        return false;
    }
}

