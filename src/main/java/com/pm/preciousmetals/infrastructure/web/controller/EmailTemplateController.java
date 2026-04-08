package com.pm.preciousmetals.infrastructure.web.controller;

import com.pm.preciousmetals.application.usecase.ManageEmailTemplateUseCase;
import com.pm.preciousmetals.domain.model.EmailTemplate;
import com.pm.preciousmetals.infrastructure.web.dto.EmailTemplateRequest;
import com.pm.preciousmetals.infrastructure.web.dto.EmailTemplateResponse;
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
    @Operation(summary = "Update email template with all its data (title, content, recipients, rules)")
    public ResponseEntity<EmailTemplateResponse> updateTemplate(
            @PathVariable UUID id,
            @Valid @RequestBody EmailTemplateRequest request) {
        log.info("REST request to update email template: {}", id);
        EmailTemplate updated = useCase.updateTemplate(request.toDomain(id));
        return ResponseEntity.ok(EmailTemplateResponse.fromDomain(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete email template")
    public ResponseEntity<Void> deleteTemplate(@PathVariable UUID id) {
        log.info("REST request to delete email template: {}", id);
        useCase.deleteTemplate(id);
        return ResponseEntity.noContent().build();
    }
}
