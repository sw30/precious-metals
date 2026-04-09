package com.pm.preciousmetals.domain.model;

import com.pm.preciousmetals.domain.model.rules.ItemRule;
import com.pm.preciousmetals.domain.model.rules.Operator;
import com.pm.preciousmetals.domain.model.rules.PriceRule;
import com.pm.preciousmetals.domain.model.rules.Rule;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;



class EmailTemplateTest {

    @Test
    void shouldUpdateTemplateData() {

        EmailTemplate template = new EmailTemplate(UUID.randomUUID(), "Old Title", "Old Content", List.of(), List.of());
        EmailRecipient recipient = new EmailRecipient("test@example.com");

        template.update("New Title", "New Content", List.of(recipient), List.of());

        assertThat(template.title()).isEqualTo("New Title");
        assertThat(template.content()).isEqualTo("New Content");
        assertThat(template.recipients()).containsExactly(recipient);
    }

    @Test
    void shouldMatchSignalBasedOnRules() {

        UUID templateId = UUID.randomUUID();
        Rule rule = new PriceRule(Operator.GREATER_THAN, new BigDecimal("100.00"));
        EmailTemplate template = new EmailTemplate(templateId, "Title", "Content", List.of(), List.of(rule));

        PriceSignal match = new PriceSignal(new Price(new BigDecimal("150.00")), MetalType.GOLD);
        PriceSignal noMatch = new PriceSignal(new Price(new BigDecimal("50.00")), MetalType.GOLD);

        assertThat(template.shouldBeSentFor(match)).isTrue();
        assertThat(template.shouldBeSentFor(noMatch)).isFalse();
    }

    @Test
    void shouldThrowExceptionWhenRulesAreMutuallyExclusive_Price() {

        Rule lessThan40 = new PriceRule(Operator.LESS_THAN, new BigDecimal("40"));
        Rule greaterThan60 = new PriceRule(Operator.GREATER_THAN, new BigDecimal("60"));
        List<Rule> rules = List.of(lessThan40, greaterThan60);

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () ->
            new EmailTemplate(UUID.randomUUID(), "Title", "Content", List.of(), rules)
        );
    }

    @Test
    void shouldThrowExceptionWhenRulesAreMutuallyExclusive_Item() {

        Rule goldRule = new ItemRule(Operator.IS_EQUAL, MetalType.GOLD);
        Rule silverRule = new ItemRule(Operator.IS_EQUAL, MetalType.SILVER);
        List<Rule> rules = List.of(goldRule, silverRule);

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () ->
            new EmailTemplate(UUID.randomUUID(), "Title", "Content", List.of(), rules)
        );
    }

    @Test
    void shouldAllowNonExclusiveRules_Price() {

        Rule greaterThan40 = new PriceRule(Operator.GREATER_THAN, new BigDecimal("40"));
        Rule lessThan60 = new PriceRule(Operator.LESS_THAN, new BigDecimal("60"));
        List<Rule> rules = List.of(greaterThan40, lessThan60);

        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() ->
            new EmailTemplate(UUID.randomUUID(), "Title", "Content", List.of(), rules)
        );
    }

    @Test
    void shouldHandleBoundaryConditionsForExclusivity() {

        Rule gte60 = new PriceRule(Operator.GREATER_THAN_EQUAL, new BigDecimal("60"));
        Rule lte60 = new PriceRule(Operator.LESS_THAN_EQUAL, new BigDecimal("60"));
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() ->
                new EmailTemplate(UUID.randomUUID(), "Title", "Content", List.of(), List.of(gte60, lte60))
        );

        Rule gt60 = new PriceRule(Operator.GREATER_THAN, new BigDecimal("60"));
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () ->
                new EmailTemplate(UUID.randomUUID(), "Title", "Content", List.of(), List.of(gt60, lte60))
        );

        Rule lt60 = new PriceRule(Operator.LESS_THAN, new BigDecimal("60"));
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () ->
                new EmailTemplate(UUID.randomUUID(), "Title", "Content", List.of(), List.of(gte60, lt60))
        );
    }
}


