package com.pm.preciousmetals.infrastructure.persistence.adapters;

import com.pm.preciousmetals.domain.model.EmailRecipient;
import com.pm.preciousmetals.domain.model.EmailTemplate;
import com.pm.preciousmetals.domain.model.MetalType;
import com.pm.preciousmetals.domain.model.rules.ItemRule;
import com.pm.preciousmetals.domain.model.rules.Operand;
import com.pm.preciousmetals.domain.model.rules.PriceRule;
import com.pm.preciousmetals.domain.model.rules.Rule;
import com.pm.preciousmetals.domain.port.EmailTemplateRepositoryPort;
import com.pm.preciousmetals.infrastructure.persistence.entity.EmailRecipientEntity;
import com.pm.preciousmetals.infrastructure.persistence.entity.EmailSendingRuleEntity;
import com.pm.preciousmetals.infrastructure.persistence.entity.EmailTemplateEntity;
import com.pm.preciousmetals.infrastructure.persistence.repository.EmailTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailTemplateRepositoryPortAdapter implements EmailTemplateRepositoryPort {

    private final EmailTemplateRepository jpaRepository;

    @Override
    public EmailTemplate save(EmailTemplate template) {
        EmailTemplateEntity entity;
        if (template.id() != null) {
            entity = jpaRepository.findById(template.id())
                    .orElseGet(EmailTemplateEntity::new);
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
        template.rules().forEach(rule -> {
            EmailSendingRuleEntity ruleEntity = new EmailSendingRuleEntity();
            if (rule instanceof PriceRule priceRule) {
                ruleEntity.setOperand(Operand.PRICE);
                ruleEntity.setOperator(priceRule.operator());
                ruleEntity.setTargetValue(priceRule.targetValue());
            } else if (rule instanceof ItemRule itemRule) {
                ruleEntity.setOperand(Operand.ITEM);
                ruleEntity.setOperator(itemRule.operator());
                ruleEntity.setTargetValue(BigDecimal.valueOf(itemRule.metalType().ordinal()));
            }
            entity.addRule(ruleEntity);
        });
    }

    @Override
    public List<EmailTemplate> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomainSafe)
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<EmailTemplate> findById(UUID id) {
        return jpaRepository.findById(id).flatMap(this::toDomainSafe);
    }

    private Optional<EmailTemplate> toDomainSafe(EmailTemplateEntity entity) {
        try {
            return Optional.of(toDomain(entity));
        } catch (Exception e) {
            log.error("Failed to map EmailTemplate entity {} to domain model: {}", entity.getId(), e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private EmailTemplate toDomain(EmailTemplateEntity entity) {
        List<EmailRecipient> recipients = entity.getRecipients().stream()
                .map(r -> new EmailRecipient(r.getEmail()))
                .collect(Collectors.toList());
        List<Rule> rules = entity.getRules().stream()
                .map(r -> switch (r.getOperand()) {
                    case PRICE -> new PriceRule(r.getOperator(), r.getTargetValue());
                    case ITEM -> new ItemRule(r.getOperator(), MetalType.values()[r.getTargetValue().intValue()]);
                })
                .collect(Collectors.toList());
        return new EmailTemplate(entity.getId(), entity.getTitle(), entity.getContent(), recipients, rules);
    }
}

