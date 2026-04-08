package com.pm.preciousmetals.domain.model;

import com.pm.preciousmetals.domain.model.rules.Rule;
import java.util.Objects;
import java.util.UUID;

public record EmailSendingRule(UUID id, Rule rule, UUID templateId) {
    public EmailSendingRule {
        Objects.requireNonNull(rule, "Rule cannot be null");
        Objects.requireNonNull(templateId, "Template ID cannot be null");
    }
}
