package com.pm.preciousmetals.infrastructure.persistence.adapters;

import com.pm.preciousmetals.domain.model.EmailRecipient;
import com.pm.preciousmetals.domain.model.EmailSendingRule;
import com.pm.preciousmetals.domain.model.EmailTemplate;
import com.pm.preciousmetals.domain.model.rules.Rule;
import com.pm.preciousmetals.infrastructure.persistence.entity.EmailRecipientEntity;
import com.pm.preciousmetals.infrastructure.persistence.entity.EmailSendingRuleEntity;
import com.pm.preciousmetals.infrastructure.persistence.entity.EmailTemplateEntity;
import com.pm.preciousmetals.infrastructure.persistence.repository.EmailTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EmailTemplateRepositoryAdapter implements com.pm.preciousmetals.domain.port.EmailTemplateRepository {

    private final EmailTemplateRepository jpaRepository;

    @Override
    public EmailTemplate save(EmailTemplate template) {
        EmailTemplateEntity entity;
        if (template.id() != null) {
            entity = jpaRepository.findById(template.id())
                    .orElseGet(() -> {
                        EmailTemplateEntity newEntity = new EmailTemplateEntity();
                        newEntity.setId(template.id());
                        return newEntity;
                    });
        } else {
            entity = new EmailTemplateEntity();
        }

        updateEntityFields(entity, template);
        EmailTemplateEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    private void updateEntityFields(EmailTemplateEntity entity, EmailTemplate template) {
        entity.setTitle(template.title());
        entity.setContent(template.content());

        entity.getRecipients().clear();
        entity.getRules().clear();

        template.recipients().forEach(r -> entity.addRecipient(new EmailRecipientEntity(r.email())));
        template.rules().forEach(r -> {
            EmailSendingRuleEntity ruleEntity = new EmailSendingRuleEntity();
            if (r.id() != null) {
                ruleEntity.setId(r.id());
            }
            ruleEntity.setOperand(r.rule().operand());
            ruleEntity.setOperator(r.rule().operator());
            ruleEntity.setTargetValue(r.rule().targetValue());
            entity.addRule(ruleEntity);
        });
    }

    @Override
    public List<EmailTemplate> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<EmailTemplate> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private EmailTemplate toDomain(EmailTemplateEntity entity) {
        List<EmailRecipient> recipients = entity.getRecipients().stream()
                .map(r -> new EmailRecipient(r.getEmail()))
                .collect(Collectors.toList());
        List<EmailSendingRule> rules = entity.getRules().stream()
                .map(r -> new EmailSendingRule(
                        r.getId(),
                        new Rule(r.getOperand(), r.getOperator(), r.getTargetValue()),
                        entity.getId()
                ))
                .collect(Collectors.toList());
        return new EmailTemplate(entity.getId(), entity.getTitle(), entity.getContent(), recipients, rules);
    }
}
