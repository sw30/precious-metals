package com.pm.preciousmetals.domain.model;

import com.pm.preciousmetals.domain.model.rules.Operand;
import com.pm.preciousmetals.domain.model.rules.Operator;
import com.pm.preciousmetals.domain.model.rules.Rule;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EmailTemplateTest {

    @Test
    void shouldCreateNewTemplateWithAddedRecipient() {
        // given
        EmailTemplate template = new EmailTemplate(UUID.randomUUID(), "Title", "Content", List.of(), List.of());
        EmailRecipient recipient = new EmailRecipient("test@example.com");

        // when
        EmailTemplate updated = template.addRecipient(recipient);

        // then
        assertThat(updated.recipients()).containsExactly(recipient);
        assertThat(template.recipients()).isEmpty(); // original unchanged
    }

    @Test
    void shouldRemoveRecipient() {
        // given
        EmailRecipient recipient = new EmailRecipient("test@example.com");
        EmailTemplate template = new EmailTemplate(UUID.randomUUID(), "Title", "Content", List.of(recipient), List.of());

        // when
        EmailTemplate updated = template.removeRecipient(recipient);

        // then
        assertThat(updated.recipients()).isEmpty();
    }

    @Test
    void shouldAddRule() {
        // given
        UUID templateId = UUID.randomUUID();
        EmailTemplate template = new EmailTemplate(templateId, "Title", "Content", List.of(), List.of());
        EmailSendingRule rule = new EmailSendingRule(UUID.randomUUID(), new Rule(Operand.PRICE, Operator.GREATER_THAN, BigDecimal.TEN), templateId);

        // when
        EmailTemplate updated = template.addRule(rule);

        // then
        assertThat(updated.rules()).containsExactly(rule);
    }

    @Test
    void shouldRemoveRule() {
        // given
        UUID templateId = UUID.randomUUID();
        UUID ruleId = UUID.randomUUID();
        EmailSendingRule rule = new EmailSendingRule(ruleId, new Rule(Operand.PRICE, Operator.GREATER_THAN, BigDecimal.TEN), templateId);
        EmailTemplate template = new EmailTemplate(templateId, "Title", "Content", List.of(), List.of(rule));

        // when
        EmailTemplate updated = template.removeRule(ruleId);

        // then
        assertThat(updated.rules()).isEmpty();
    }
}
