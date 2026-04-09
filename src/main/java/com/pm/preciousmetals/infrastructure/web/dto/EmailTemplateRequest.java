package com.pm.preciousmetals.infrastructure.web.dto;

import com.pm.preciousmetals.domain.model.EmailRecipient;
import com.pm.preciousmetals.domain.model.EmailTemplate;
import com.pm.preciousmetals.domain.model.rules.Rule;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public record EmailTemplateRequest(
    @NotBlank(message = "Title is required")
    String title,
    @NotBlank(message = "Content is required")
    String content,
    @Valid
    List<EmailRecipientDto> recipients,
    @Valid
    List<EmailSendingRuleDto> rules
) {
    public EmailTemplate toDomain() {
        return toDomain(UUID.randomUUID());
    }

    public EmailTemplate toDomain(UUID templateId) {
        List<EmailRecipient> domainRecipients = recipients != null ? 
            recipients.stream().map(EmailRecipientDto::toDomain).toList() : Collections.emptyList();
        List<Rule> domainRules = rules != null ? 
            rules.stream().map(EmailSendingRuleDto::toDomain).toList() : Collections.emptyList();
        
        return new EmailTemplate(templateId, title, content, domainRecipients, domainRules);
    }
}

