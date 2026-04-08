package com.pm.preciousmetals.infrastructure.adapter.persistence;

import com.pm.preciousmetals.domain.model.EmailRecipient;
import com.pm.preciousmetals.domain.model.EmailSendingRule;
import com.pm.preciousmetals.domain.model.EmailTemplate;
import com.pm.preciousmetals.domain.model.rules.Operand;
import com.pm.preciousmetals.domain.model.rules.Operator;
import com.pm.preciousmetals.domain.model.rules.Rule;
import com.pm.preciousmetals.infrastructure.adapter.persistence.repository.EmailTemplateRepository;
import com.pm.preciousmetals.infrastructure.adapter.persistence.repository_adapters.EmailTemplateRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class EmailTemplatePersistenceIT {

    @Autowired
    private EmailTemplateRepositoryAdapter adapter;

    @Autowired
    private EmailTemplateRepository jpaRepository;

    @BeforeEach
    void setUp() {
        jpaRepository.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveTemplate() {
        // given
        EmailTemplate template = new EmailTemplate(null, "Title", "Content", List.of(), List.of());

        // when
        EmailTemplate saved = adapter.save(template);

        // then
        assertThat(saved.id()).isNotNull();
        Optional<EmailTemplate> retrieved = adapter.findById(saved.id());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().title()).isEqualTo("Title");
    }

    @Test
    void shouldUpdateTemplateWithRecipientsAndRules() {
        // given
        EmailTemplate template = adapter.save(new EmailTemplate(null, "Initial", "Initial", List.of(), List.of()));
        EmailRecipient recipient = new EmailRecipient("test@example.com");
        EmailSendingRule rule = new EmailSendingRule(null, new Rule(Operand.PRICE, Operator.GREATER_THAN, BigDecimal.TEN), template.id());

        // when
        EmailTemplate updated = template.addRecipient(recipient).addRule(rule).withTitle("Updated");
        adapter.save(updated);

        // then
        EmailTemplate retrieved = adapter.findById(template.id()).orElseThrow();
        assertThat(retrieved.title()).isEqualTo("Updated");
        assertThat(retrieved.recipients()).hasSize(1);
        assertThat(retrieved.recipients().get(0).email()).isEqualTo("test@example.com");
        assertThat(retrieved.rules()).hasSize(1);
        assertThat(retrieved.rules().get(0).rule().targetValue()).isEqualByComparingTo(BigDecimal.TEN);
    }

    @Test
    void shouldRemoveRecipientsAndRulesViaOrphanRemoval() {
        // given
        EmailTemplate template = new EmailTemplate(null, "Title", "Content", List.of(), List.of());
        EmailRecipient recipient = new EmailRecipient("test@example.com");

        EmailTemplate saved = adapter.save(template);
        // Add recipient and rule
        EmailSendingRule rule = new EmailSendingRule(null, new Rule(Operand.PRICE, Operator.GREATER_THAN, BigDecimal.TEN), saved.id());
        EmailTemplate updated = saved.addRecipient(recipient).addRule(rule);
        EmailTemplate persisted = adapter.save(updated);
        assertThat(persisted.recipients()).hasSize(1);
        assertThat(persisted.rules()).hasSize(1);

        // when - remove both
        UUID ruleId = persisted.rules().get(0).id();
        EmailTemplate afterRemoval = persisted.removeRecipient(recipient).removeRule(ruleId);
        adapter.save(afterRemoval);

        // then
        EmailTemplate retrieved = adapter.findById(saved.id()).orElseThrow();
        assertThat(retrieved.recipients()).isEmpty();
        assertThat(retrieved.rules()).isEmpty();
    }
}
