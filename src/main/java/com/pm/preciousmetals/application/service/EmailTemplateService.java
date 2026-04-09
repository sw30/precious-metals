package com.pm.preciousmetals.application.service;

import com.pm.preciousmetals.application.usecase.ManageEmailTemplateUseCase;
import com.pm.preciousmetals.domain.exception.ResourceNotFoundException;
import com.pm.preciousmetals.domain.model.EmailTemplate;
import com.pm.preciousmetals.domain.port.EmailTemplateRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Slf4j
@RequiredArgsConstructor
public class EmailTemplateService implements ManageEmailTemplateUseCase {

    private final EmailTemplateRepositoryPort repository;

    @Override
    public EmailTemplate createTemplate(EmailTemplate template) {
        log.info("Creating new email template: {}", template.title());
        return repository.save(template);
    }

    @Override
    public EmailTemplate updateTemplate(EmailTemplate updatedTemplate) {
        log.info("Updating email template: {}", updatedTemplate.id());
        EmailTemplate existing = repository.findById(updatedTemplate.id())
                .orElseThrow(() -> new ResourceNotFoundException("Email template not found: " + updatedTemplate.id()));
        
        existing.update(updatedTemplate.title(), updatedTemplate.content(), updatedTemplate.recipients(), updatedTemplate.rules());
        return repository.save(existing);
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
}

