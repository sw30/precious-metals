package com.pm.preciousmetals.domain.model.rules;

import com.pm.preciousmetals.domain.model.MetalType;
import com.pm.preciousmetals.domain.model.Price;
import com.pm.preciousmetals.domain.model.PriceSignal;
import java.math.BigDecimal;
import java.util.Objects;

public record PriceRule(Operator operator, BigDecimal targetValue) implements Rule {
    public PriceRule {
        Objects.requireNonNull(operator, "Operator is mandatory");
        Objects.requireNonNull(targetValue, "Target value is mandatory");
    }

    @Override
    public boolean matches(PriceSignal signal) {
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
    }

    @Override
    public boolean isExclusive(Rule other) {
        if (other instanceof PriceRule otherPrice) {
            if (this.operator == Operator.IS_EQUAL) {
                return !otherPrice.matches(new PriceSignal(new Price(this.targetValue), MetalType.GOLD));
            }
            if (otherPrice.operator == Operator.IS_EQUAL) {
                return !this.matches(new PriceSignal(new Price(otherPrice.targetValue), MetalType.GOLD));
            }

            if (this.operator == Operator.IS_NOT_EQUAL || otherPrice.operator == Operator.IS_NOT_EQUAL) {
                return false;
            }

            if (this.operator.isGreater() && otherPrice.operator.isGreater()) return false;
            if (this.operator.isLess() && otherPrice.operator.isLess()) return false;

            PriceRule greater = this.operator.isGreater() ? this : otherPrice;
            PriceRule less = this.operator.isLess() ? this : otherPrice;

            int cmp = greater.targetValue.compareTo(less.targetValue);
            if (cmp > 0) return true;
            if (cmp == 0) {
                return !(greater.operator == Operator.GREATER_THAN_EQUAL && less.operator == Operator.LESS_THAN_EQUAL);
            }
        }
        return false;
    }
}

