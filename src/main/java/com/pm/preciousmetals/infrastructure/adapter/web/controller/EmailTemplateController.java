package com.pm.preciousmetals.infrastructure.adapter.web.controller;

import com.pm.preciousmetals.application.usecase.ManageEmailTemplateUseCase;
import com.pm.preciousmetals.domain.model.EmailRecipient;
import com.pm.preciousmetals.domain.model.EmailSendingRule;
import com.pm.preciousmetals.domain.model.EmailTemplate;
import com.pm.preciousmetals.infrastructure.adapter.web.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/api/v1/email-templates")
@RequiredArgsConstructor
@Tag(name = "Email Templates", description = "Endpoints for managing email templates, recipients and sending rules")
public class EmailTemplateController {

    private final ManageEmailTemplateUseCase useCase;

    @PostMapping
    @Operation(summary = "Create a new email template with all its data")
    public ResponseEntity<EmailTemplateResponse> createTemplate(@Valid @RequestBody EmailTemplateRequest request) {
        log.info("REST request to create email template: {}", request.title());
        EmailTemplate template = request.toDomain();
        EmailTemplate created = useCase.createTemplate(template);
        return ResponseEntity.status(HttpStatus.CREATED).body(EmailTemplateResponse.fromDomain(created));
    }

    @GetMapping
    @Operation(summary = "Get all email templates")
    public ResponseEntity<List<EmailTemplateResponse>> getAllTemplates() {
        log.info("REST request to get all email templates");
        List<EmailTemplate> templates = useCase.getAllTemplates();
        return ResponseEntity.ok(templates.stream().map(EmailTemplateResponse::fromDomain).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get email template by ID")
    public ResponseEntity<EmailTemplateResponse> getTemplate(@PathVariable UUID id) {
        log.info("REST request to get email template: {}", id);
        return useCase.getTemplate(id)
                .map(EmailTemplateResponse::fromDomain)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update email template fields (title, content)")
    public ResponseEntity<EmailTemplateResponse> updateTemplate(
            @PathVariable UUID id,
            @Valid @RequestBody EmailTemplateRequest request) {
        log.info("REST request to update email template: {}", id);
        EmailTemplate updated = useCase.updateTemplate(id, request.title(), request.content());
        return ResponseEntity.ok(EmailTemplateResponse.fromDomain(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete email template")
    public ResponseEntity<Void> deleteTemplate(@PathVariable UUID id) {
        log.info("REST request to delete email template: {}", id);
        useCase.deleteTemplate(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/recipients")
    @Operation(summary = "Add a new recipient to the template")
    public ResponseEntity<EmailTemplateResponse> addRecipient(
            @PathVariable UUID id,
            @Valid @RequestBody EmailRecipientDto recipientDto) {
        log.info("REST request to add recipient to template: {}", id);
        EmailTemplate updated = useCase.addRecipient(id, recipientDto.toDomain());
        return ResponseEntity.ok(EmailTemplateResponse.fromDomain(updated));
    }

    @DeleteMapping("/{id}/recipients/{email}")
    @Operation(summary = "Remove a recipient from the template by email")
    public ResponseEntity<EmailTemplateResponse> removeRecipient(
            @PathVariable UUID id,
            @PathVariable String email) {
        log.info("REST request to remove recipient {} from template: {}", email, id);
        EmailTemplate updated = useCase.removeRecipient(id, email);
        return ResponseEntity.ok(EmailTemplateResponse.fromDomain(updated));
    }

    @PostMapping("/{id}/rules")
    @Operation(summary = "Add a new sending rule to the template")
    public ResponseEntity<EmailTemplateResponse> addRule(
            @PathVariable UUID id,
            @Valid @RequestBody EmailSendingRuleDto ruleDto) {
        log.info("REST request to add rule to template: {}", id);
        EmailSendingRule rule = ruleDto.toDomain(id);
        EmailTemplate updated = useCase.addRule(id, rule);
        return ResponseEntity.ok(EmailTemplateResponse.fromDomain(updated));
    }

    @DeleteMapping("/{id}/rules/{ruleId}")
    @Operation(summary = "Remove a sending rule from the template")
    public ResponseEntity<EmailTemplateResponse> removeRule(
            @PathVariable UUID id,
            @PathVariable UUID ruleId) {
        log.info("REST request to remove rule {} from template: {}", ruleId, id);
        EmailTemplate updated = useCase.removeRule(id, ruleId);
        return ResponseEntity.ok(EmailTemplateResponse.fromDomain(updated));
    }
}
