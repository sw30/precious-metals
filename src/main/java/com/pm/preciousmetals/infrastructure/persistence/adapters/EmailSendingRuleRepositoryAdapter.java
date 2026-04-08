package com.pm.preciousmetals.infrastructure.persistence.adapters;

import com.pm.preciousmetals.domain.model.EmailSendingRule;
import com.pm.preciousmetals.domain.model.rules.Rule;
import com.pm.preciousmetals.infrastructure.persistence.entity.EmailSendingRuleEntity;
import com.pm.preciousmetals.infrastructure.persistence.entity.EmailTemplateEntity;
import com.pm.preciousmetals.infrastructure.persistence.repository.EmailSendingRuleRepository;
import com.pm.preciousmetals.infrastructure.persistence.repository.EmailTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EmailSendingRuleRepositoryAdapter implements com.pm.preciousmetals.domain.port.EmailSendingRuleRepository {

    private final EmailSendingRuleRepository jpaRepository;
    private final EmailTemplateRepository jpaTemplateRepository;

    @Override
    public EmailSendingRule save(EmailSendingRule rule) {
        EmailSendingRuleEntity entity = toEntity(rule);
        EmailSendingRuleEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<EmailSendingRule> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<EmailSendingRule> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private EmailSendingRuleEntity toEntity(EmailSendingRule rule) {
        EmailSendingRuleEntity entity = new EmailSendingRuleEntity();
        if (rule.id() != null) {
            entity.setId(rule.id());
        }
        entity.setOperand(rule.rule().operand());
        entity.setOperator(rule.rule().operator());
        entity.setTargetValue(rule.rule().targetValue());

        EmailTemplateEntity templateEntity = jpaTemplateRepository.findById(rule.templateId())
                .orElseThrow(() -> new IllegalArgumentException("Template not found: " + rule.templateId()));
        entity.setTemplate(templateEntity);

        return entity;
    }

    private EmailSendingRule toDomain(EmailSendingRuleEntity entity) {
        Rule domainRule = new Rule(entity.getOperand(), entity.getOperator(), entity.getTargetValue());
        return new EmailSendingRule(entity.getId(), domainRule, entity.getTemplate().getId());
    }
}
