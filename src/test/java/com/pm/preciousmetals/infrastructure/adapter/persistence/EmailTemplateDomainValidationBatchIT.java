package com.pm.preciousmetals.infrastructure.adapter.persistence;

import com.pm.preciousmetals.domain.model.EmailTemplate;
import com.pm.preciousmetals.domain.model.rules.Operand;
import com.pm.preciousmetals.domain.model.rules.Operator;
import com.pm.preciousmetals.infrastructure.persistence.adapters.EmailTemplateRepositoryPortAdapter;
import com.pm.preciousmetals.infrastructure.persistence.entity.EmailSendingRuleEntity;
import com.pm.preciousmetals.infrastructure.persistence.entity.EmailTemplateEntity;
import com.pm.preciousmetals.infrastructure.persistence.repository.EmailTemplateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class EmailTemplateDomainValidationBatchIT {

    @Autowired
    private EmailTemplateRepositoryPortAdapter adapter;

    @Autowired
    private EmailTemplateRepository jpaRepository;

    @BeforeEach
    void setUp() {
        jpaRepository.deleteAll();
    }

    @Test
    void findAllShouldSkipInvalidDataWhenDatabaseContainsInvalidData() {
        // Given: valid template
        EmailTemplateEntity validEntity = new EmailTemplateEntity();
        validEntity.setTitle("Valid");
        validEntity.setContent("Valid Content");
        jpaRepository.save(validEntity);

        // Given: invalid template (mutually exclusive rules)
        EmailTemplateEntity invalidEntity = new EmailTemplateEntity();
        invalidEntity.setTitle("Invalid");
        invalidEntity.setContent("Invalid Content");
        
        EmailSendingRuleEntity rule1 = new EmailSendingRuleEntity();
        rule1.setOperand(Operand.PRICE);
        rule1.setOperator(Operator.IS_EQUAL);
        rule1.setTargetValue(BigDecimal.TEN);
        invalidEntity.addRule(rule1);

        EmailSendingRuleEntity rule2 = new EmailSendingRuleEntity();
        rule2.setOperand(Operand.PRICE);
        rule2.setOperator(Operator.IS_EQUAL);
        rule2.setTargetValue(BigDecimal.ONE);
        invalidEntity.addRule(rule2);

        jpaRepository.save(invalidEntity);

        // When: findAll is called
        List<EmailTemplate> all = adapter.findAll();

        // Then: it should only contain the valid template
        assertThat(all).hasSize(1);
        assertThat(all.get(0).title()).isEqualTo("Valid");
    }

    @Test
    void findByIdShouldReturnEmptyWhenDataIsInvalid() {
        // Given: invalid template (mutually exclusive rules)
        EmailTemplateEntity invalidEntity = new EmailTemplateEntity();
        invalidEntity.setTitle("Invalid");
        invalidEntity.setContent("Invalid Content");
        
        EmailSendingRuleEntity rule1 = new EmailSendingRuleEntity();
        rule1.setOperand(Operand.PRICE);
        rule1.setOperator(Operator.IS_EQUAL);
        rule1.setTargetValue(BigDecimal.TEN);
        invalidEntity.addRule(rule1);

        EmailSendingRuleEntity rule2 = new EmailSendingRuleEntity();
        rule2.setOperand(Operand.PRICE);
        rule2.setOperator(Operator.IS_EQUAL);
        rule2.setTargetValue(BigDecimal.ONE);
        invalidEntity.addRule(rule2);

        EmailTemplateEntity saved = jpaRepository.save(invalidEntity);

        // When: findById is called
        Optional<EmailTemplate> result = adapter.findById(saved.getId());

        // Then: it should be empty
        assertThat(result).isEmpty();
    }
}
