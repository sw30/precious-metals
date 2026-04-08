package com.pm.preciousmetals.infrastructure.adapter.web.controller;

import com.pm.preciousmetals.application.usecase.ManageEmailTemplateUseCase;
import com.pm.preciousmetals.domain.model.EmailRecipient;
import com.pm.preciousmetals.domain.model.EmailSendingRule;
import com.pm.preciousmetals.domain.model.EmailTemplate;
import com.pm.preciousmetals.domain.model.rules.Operand;
import com.pm.preciousmetals.domain.model.rules.Operator;
import com.pm.preciousmetals.domain.model.rules.Rule;
import com.pm.preciousmetals.infrastructure.adapter.web.error.GlobalExceptionHandler;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EmailTemplateControllerTest {

    private MockMvc mockMvc;
    private final Tracer tracer = mock(Tracer.class);
    private final HttpServletRequest request = mock(HttpServletRequest.class);
    private final ManageEmailTemplateUseCase useCase = mock(ManageEmailTemplateUseCase.class);

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new EmailTemplateController(useCase))
                .setControllerAdvice(new GlobalExceptionHandler(Optional.of(tracer), request))
                .build();
    }

    @Test
    void shouldCreateTemplate() throws Exception {
        UUID id = UUID.randomUUID();
        EmailTemplate template = new EmailTemplate(id, "Title", "Content", Collections.emptyList(), Collections.emptyList());
        when(useCase.createTemplate(any())).thenReturn(template);

        String json = """
                {
                    "title": "Title",
                    "content": "Content"
                }
                """;

        mockMvc.perform(post("/api/v1/email-templates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String response = result.getResponse().getContentAsString();
                    assertThat(response).contains("\"id\":\"" + id + "\"");
                    assertThat(response).contains("\"title\":\"Title\"");
                });
    }

    @Test
    void shouldGetTemplate() throws Exception {
        UUID id = UUID.randomUUID();
        EmailTemplate template = new EmailTemplate(id, "Title", "Content", 
                List.of(new EmailRecipient("test@example.com")), Collections.emptyList());
        when(useCase.getTemplate(id)).thenReturn(Optional.of(template));

        mockMvc.perform(get("/api/v1/email-templates/" + id))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String response = result.getResponse().getContentAsString();
                    assertThat(response).contains("\"email\":\"test@example.com\"");
                });
    }

    @Test
    void shouldAddRecipient() throws Exception {
        UUID id = UUID.randomUUID();
        EmailTemplate updatedTemplate = new EmailTemplate(id, "Title", "Content", 
                List.of(new EmailRecipient("new@example.com")), Collections.emptyList());
        when(useCase.addRecipient(eq(id), any())).thenReturn(updatedTemplate);

        String json = "{\"email\": \"new@example.com\"}";

        mockMvc.perform(post("/api/v1/email-templates/" + id + "/recipients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String response = result.getResponse().getContentAsString();
                    assertThat(response).contains("\"email\":\"new@example.com\"");
                });
    }

    @Test
    void shouldAddRule() throws Exception {
        UUID id = UUID.randomUUID();
        UUID ruleId = UUID.randomUUID();
        EmailSendingRule rule = new EmailSendingRule(ruleId, new Rule(Operand.PRICE, Operator.GREATER_THAN, new BigDecimal("100.00")), id);
        EmailTemplate updatedTemplate = new EmailTemplate(id, "Title", "Content", 
                Collections.emptyList(), List.of(rule));
        
        when(useCase.addRule(eq(id), any())).thenReturn(updatedTemplate);

        String json = """
                {
                    "operand": "PRICE",
                    "operator": "GREATER_THAN",
                    "targetValue": 100.00
                }
                """;

        mockMvc.perform(post("/api/v1/email-templates/" + id + "/rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String response = result.getResponse().getContentAsString();
                    assertThat(response).contains("\"operand\":\"PRICE\"");
                    assertThat(response).contains("\"targetValue\":100.0");
                });
    }

    @Test
    void shouldDeleteTemplate() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/email-templates/" + id))
                .andExpect(status().isNoContent());

        verify(useCase).deleteTemplate(id);
    }
}
