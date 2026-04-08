package com.pm.preciousmetals.infrastructure.adapter.persistence;

import com.pm.preciousmetals.domain.model.EmailTemplate;
import com.pm.preciousmetals.infrastructure.adapter.persistence.repository_adapters.EmailTemplateRepositoryAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SeedDataIT {

    @Autowired
    private EmailTemplateRepositoryAdapter adapter;

    @Test
    void shouldHaveSeededDataAtStartup() {
        // when
        List<EmailTemplate> templates = adapter.findAll();

        // then
        assertThat(templates).hasSizeGreaterThanOrEqualTo(2);
        
        EmailTemplate goldTemplate = adapter.findById(UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")).orElseThrow();
        assertThat(goldTemplate.title()).isEqualTo("Gold Price Alert");
        assertThat(goldTemplate.recipients()).hasSize(2);
        assertThat(goldTemplate.rules()).hasSize(1);
    }
}
