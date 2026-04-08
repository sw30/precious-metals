package com.pm.preciousmetals.infrastructure.adapter.web.controller;

import com.pm.preciousmetals.application.usecase.ManageEmailTemplateUseCase;
import com.pm.preciousmetals.domain.model.EmailRecipient;
import com.pm.preciousmetals.domain.model.EmailSendingRule;
import com.pm.preciousmetals.domain.model.EmailTemplate;
import com.pm.preciousmetals.domain.model.rules.Operand;
import com.pm.preciousmetals.domain.model.rules.Operator;
import com.pm.preciousmetals.domain.model.rules.Rule;
import com.pm.preciousmetals.infrastructure.web.controller.EmailTemplateController;
import com.pm.preciousmetals.infrastructure.web.error.GlobalExceptionHandler;
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
    void shouldUpdateTemplate() throws Exception {
        UUID id = UUID.randomUUID();
        EmailTemplate updatedTemplate = new EmailTemplate(id, "Updated Title", "Updated Content", 
                List.of(new EmailRecipient("updated@example.com")), Collections.emptyList());
        
        when(useCase.updateTemplate(any())).thenReturn(updatedTemplate);

        String json = """
                {
                    "title": "Updated Title",
                    "content": "Updated Content",
                    "recipients": [{"email": "updated@example.com"}],
                    "rules": []
                }
                """;

        mockMvc.perform(put("/api/v1/email-templates/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String response = result.getResponse().getContentAsString();
                    assertThat(response).contains("\"title\":\"Updated Title\"");
                    assertThat(response).contains("\"email\":\"updated@example.com\"");
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
