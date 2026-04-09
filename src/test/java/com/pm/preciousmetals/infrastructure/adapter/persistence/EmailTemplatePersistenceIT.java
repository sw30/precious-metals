package com.pm.preciousmetals.infrastructure.adapter.persistence;

import com.pm.preciousmetals.domain.model.EmailRecipient;
import com.pm.preciousmetals.domain.model.EmailTemplate;
import com.pm.preciousmetals.domain.model.MetalType;
import com.pm.preciousmetals.domain.model.rules.ItemRule;
import com.pm.preciousmetals.domain.model.rules.Operator;
import com.pm.preciousmetals.domain.model.rules.PriceRule;
import com.pm.preciousmetals.domain.model.rules.Rule;
import com.pm.preciousmetals.infrastructure.persistence.repository.EmailTemplateRepository;
import com.pm.preciousmetals.infrastructure.persistence.adapters.EmailTemplateRepositoryPortAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;



@SpringBootTest
@Transactional
class EmailTemplatePersistenceIT {

    @Autowired
    private EmailTemplateRepositoryPortAdapter adapter;

    @Autowired
    private EmailTemplateRepository jpaRepository;

    @BeforeEach
    void setUp() {
        jpaRepository.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveTemplate() {

        EmailTemplate template = new EmailTemplate(null, "Title", "Content", List.of(), List.of());

        EmailTemplate saved = adapter.save(template);

        assertThat(saved.id()).isNotNull();
        Optional<EmailTemplate> retrieved = adapter.findById(saved.id());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().title()).isEqualTo("Title");
    }

    @Test
    void shouldUpdateTemplateWithRecipientsAndRules() {

        EmailTemplate template = adapter.save(new EmailTemplate(null, "Initial", "Initial", List.of(), List.of()));
        EmailRecipient recipient = new EmailRecipient("test@example.com");
        Rule priceRule = new PriceRule(Operator.GREATER_THAN, BigDecimal.TEN);
        Rule itemRule = new ItemRule(Operator.IS_EQUAL, MetalType.SILVER);

        template.update("Updated", "Initial", List.of(recipient), List.of(priceRule, itemRule));
        adapter.save(template);

        EmailTemplate retrieved = adapter.findById(template.id()).orElseThrow();
        assertThat(retrieved.title()).isEqualTo("Updated");
        assertThat(retrieved.recipients()).hasSize(1);
        assertThat(retrieved.recipients().get(0).email()).isEqualTo("test@example.com");
        assertThat(retrieved.rules()).hasSize(2);
        
        PriceRule retrievedPriceRule = (PriceRule) retrieved.rules().stream()
                .filter(r -> r instanceof PriceRule)
                .findFirst().get();
        assertThat(retrievedPriceRule.targetValue()).isEqualByComparingTo(BigDecimal.TEN);

        ItemRule retrievedItemRule = (ItemRule) retrieved.rules().stream()
                .filter(r -> r instanceof ItemRule)
                .findFirst().get();
        assertThat(retrievedItemRule.metalType()).isEqualTo(MetalType.SILVER);
    }

    @Test
    void shouldRemoveRecipientsAndRulesViaOrphanRemoval() {

        EmailTemplate template = new EmailTemplate(null, "Title", "Content", List.of(), List.of());
        EmailRecipient recipient = new EmailRecipient("test@example.com");

        EmailTemplate saved = adapter.save(template);

        Rule rule = new PriceRule(Operator.GREATER_THAN, BigDecimal.TEN);
        saved.update("Title", "Content", List.of(recipient), List.of(rule));
        EmailTemplate persisted = adapter.save(saved);
        assertThat(persisted.recipients()).hasSize(1);
        assertThat(persisted.rules()).hasSize(1);

        persisted.update("Title", "Content", List.of(), List.of());
        adapter.save(persisted);

        EmailTemplate retrieved = adapter.findById(saved.id()).orElseThrow();
        assertThat(retrieved.recipients()).isEmpty();
        assertThat(retrieved.rules()).isEmpty();
    }
}

