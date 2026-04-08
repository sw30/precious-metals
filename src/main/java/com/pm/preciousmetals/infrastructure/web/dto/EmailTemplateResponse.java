package com.pm.preciousmetals.infrastructure.web.dto;

import com.pm.preciousmetals.domain.model.EmailTemplate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public record EmailTemplateResponse(
    UUID id,
    String title,
    String content,
    List<EmailRecipientDto> recipients,
    List<EmailSendingRuleDto> rules
) {
    public static EmailTemplateResponse fromDomain(EmailTemplate template) {
        return new EmailTemplateResponse(
            template.id(),
            template.title(),
            template.content(),
            template.recipients() != null ? template.recipients().stream().map(EmailRecipientDto::fromDomain).toList() : Collections.emptyList(),
            template.rules() != null ? template.rules().stream().map(EmailSendingRuleDto::fromDomain).toList() : Collections.emptyList()
        );
    }
}
