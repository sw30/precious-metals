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
    void shouldUpdateTemplateData() {
        // given
        EmailTemplate template = new EmailTemplate(UUID.randomUUID(), "Old Title", "Old Content", List.of(), List.of());
        EmailRecipient recipient = new EmailRecipient("test@example.com");

        // when
        template.update("New Title", "New Content", List.of(recipient), List.of());

        // then
        assertThat(template.title()).isEqualTo("New Title");
        assertThat(template.content()).isEqualTo("New Content");
        assertThat(template.recipients()).containsExactly(recipient);
    }

    @Test
    void shouldMatchSignalBasedOnRules() {
        // given
        UUID templateId = UUID.randomUUID();
        Rule rule = new Rule(Operand.PRICE, Operator.GREATER_THAN, new BigDecimal("100.00"));
        EmailSendingRule emailRule = new EmailSendingRule(UUID.randomUUID(), rule, templateId);
        EmailTemplate template = new EmailTemplate(templateId, "Title", "Content", List.of(), List.of(emailRule));

        PriceSignal match = new PriceSignal(new Price(new BigDecimal("150.00")), MetalType.GOLD);
        PriceSignal noMatch = new PriceSignal(new Price(new BigDecimal("50.00")), MetalType.GOLD);

        // when & then
        assertThat(template.shouldBeSentFor(match)).isTrue();
        assertThat(template.shouldBeSentFor(noMatch)).isFalse();
    }
}
