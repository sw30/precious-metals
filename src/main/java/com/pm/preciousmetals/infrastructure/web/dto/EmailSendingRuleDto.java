package com.pm.preciousmetals.infrastructure.web.dto;

import com.pm.preciousmetals.domain.model.MetalType;
import com.pm.preciousmetals.domain.model.rules.ItemRule;
import com.pm.preciousmetals.domain.model.rules.Operand;
import com.pm.preciousmetals.domain.model.rules.Operator;
import com.pm.preciousmetals.domain.model.rules.PriceRule;
import com.pm.preciousmetals.domain.model.rules.Rule;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record EmailSendingRuleDto(
    @NotNull(message = "Operand is required")
    Operand operand,
    @NotNull(message = "Operator is required")
    Operator operator,
    BigDecimal targetValue,
    MetalType metalType
) {
    public static EmailSendingRuleDto fromDomain(Rule rule) {
        if (rule instanceof PriceRule(Operator operator2, BigDecimal value)) {
            return new EmailSendingRuleDto(
                Operand.PRICE,
                    operator2,
                    value,
                null
            );
        } else if (rule instanceof ItemRule(Operator operator1, MetalType type)) {
            return new EmailSendingRuleDto(
                Operand.ITEM,
                    operator1,
                null,
                    type
            );
        }
        throw new IllegalArgumentException("Unsupported rule type: " + rule.getClass());
    }

    public Rule toDomain() {
        return switch (operand) {
            case PRICE -> new PriceRule(operator, targetValue);
            case ITEM -> new ItemRule(operator, metalType);
        };
    }
}

