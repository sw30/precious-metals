package com.pm.preciousmetals.infrastructure.web.dto;

import com.pm.preciousmetals.domain.model.EmailSendingRule;
import com.pm.preciousmetals.domain.model.rules.Operand;
import com.pm.preciousmetals.domain.model.rules.Operator;
import com.pm.preciousmetals.domain.model.rules.Rule;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record EmailSendingRuleDto(
    UUID id,
    @NotNull(message = "Operand is required")
    Operand operand,
    @NotNull(message = "Operator is required")
    Operator operator,
    @NotNull(message = "Target value is required")
    BigDecimal targetValue
) {
    public static EmailSendingRuleDto fromDomain(EmailSendingRule domainRule) {
        return new EmailSendingRuleDto(
            domainRule.id(),
            domainRule.rule().operand(),
            domainRule.rule().operator(),
            domainRule.rule().targetValue()
        );
    }

    public EmailSendingRule toDomain(UUID templateId) {
        return new EmailSendingRule(id, new Rule(operand, operator, targetValue), templateId);
    }
}
