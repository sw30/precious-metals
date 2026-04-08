package com.pm.preciousmetals.application.service;

import com.pm.preciousmetals.application.usecase.ManageEmailTemplateUseCase;
import com.pm.preciousmetals.domain.exception.ResourceNotFoundException;
import com.pm.preciousmetals.domain.model.EmailRecipient;
import com.pm.preciousmetals.domain.model.EmailSendingRule;
import com.pm.preciousmetals.domain.model.EmailTemplate;
import com.pm.preciousmetals.domain.port.EmailTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class EmailTemplateService implements ManageEmailTemplateUseCase {

    private final EmailTemplateRepository repository;

    @Override
    public EmailTemplate createTemplate(EmailTemplate template) {
        log.info("Creating new email template: {}", template.title());
        return repository.save(template);
    }

    @Override
    public EmailTemplate updateTemplate(UUID id, String title, String content) {
        log.info("Updating email template: {}", id);
        EmailTemplate template = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Email template not found: " + id));
        
        EmailTemplate updated = template.withTitle(title).withContent(content);
        return repository.save(updated);
    }

    @Override
    public void deleteTemplate(UUID id) {
        log.info("Deleting email template: {}", id);
        repository.deleteById(id);
    }

    @Override
    public Optional<EmailTemplate> getTemplate(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<EmailTemplate> getAllTemplates() {
        return repository.findAll();
    }

    @Override
    public EmailTemplate addRecipient(UUID templateId, EmailRecipient recipient) {
        log.info("Adding recipient {} to template {}", recipient.email(), templateId);
        EmailTemplate template = repository.findById(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("Email template not found: " + templateId));
        
        EmailTemplate updated = template.addRecipient(recipient);
        return repository.save(updated);
    }

    @Override
    public EmailTemplate removeRecipient(UUID templateId, String email) {
        log.info("Removing recipient {} from template {}", email, templateId);
        EmailTemplate template = repository.findById(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("Email template not found: " + templateId));
        
        EmailTemplate updated = template.removeRecipient(new EmailRecipient(email));
        return repository.save(updated);
    }

    @Override
    public EmailTemplate addRule(UUID templateId, EmailSendingRule rule) {
        log.info("Adding rule to template {}", templateId);
        EmailTemplate template = repository.findById(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("Email template not found: " + templateId));
        
        // Ensure the rule is associated with this template ID
        EmailSendingRule ruleWithId = new EmailSendingRule(rule.id(), rule.rule(), templateId);
        EmailTemplate updated = template.addRule(ruleWithId);
        return repository.save(updated);
    }

    @Override
    public EmailTemplate removeRule(UUID templateId, UUID ruleId) {
        log.info("Removing rule {} from template {}", ruleId, templateId);
        EmailTemplate template = repository.findById(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("Email template not found: " + templateId));
        
        EmailTemplate updated = template.removeRule(ruleId);
        return repository.save(updated);
    }
}
