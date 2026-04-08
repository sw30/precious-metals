package com.pm.preciousmetals.application.usecase;

import com.pm.preciousmetals.domain.model.EmailRecipient;
import com.pm.preciousmetals.domain.model.EmailSendingRule;
import com.pm.preciousmetals.domain.model.EmailTemplate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ManageEmailTemplateUseCase {
    EmailTemplate createTemplate(EmailTemplate template);
    EmailTemplate updateTemplate(EmailTemplate template);
    void deleteTemplate(UUID id);
    Optional<EmailTemplate> getTemplate(UUID id);
    List<EmailTemplate> getAllTemplates();
}
