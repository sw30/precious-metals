package com.pm.preciousmetals.infrastructure.adapter.web.controller;

import com.pm.preciousmetals.application.usecase.ProcessPriceSignalUseCase;
import com.pm.preciousmetals.infrastructure.adapter.web.error.GlobalExceptionHandler;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PriceSignalControllerTest {

    private MockMvc mockMvc;
    private final Tracer tracer = mock(Tracer.class);
    private final HttpServletRequest request = mock(HttpServletRequest.class);
    private final ProcessPriceSignalUseCase processPriceSignalUseCase = mock(ProcessPriceSignalUseCase.class);

    @BeforeEach
    void setUp() {
        when(processPriceSignalUseCase.processPriceSignal(any())).thenAnswer(invocation -> invocation.getArgument(0));
        mockMvc = MockMvcBuilders.standaloneSetup(new PriceSignalController(processPriceSignalUseCase))
                .setControllerAdvice(new GlobalExceptionHandler(Optional.of(tracer), request))
                .build();
    }

    @Test
    void shouldAcceptValidPriceSignal() throws Exception {
        String json = """
                {
                "itemType": "gold",
                "price": "32323.32"
                }
                """;

        mockMvc.perform(post("/api/v1/price-signal/new-price")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(result -> {
                    String response = result.getResponse().getContentAsString();
                    assertThat(response).contains("\"metalType\":\"GOLD\"");
                    assertThat(response).contains("\"price\":{\"value\":32323.32}");
                });
    }

    @Test
    void shouldRejectInvalidMetalType() throws Exception {
        String json = """
                {
                "itemType": "copper",
                "price": "32323.32"
                }
                """;

        mockMvc.perform(post("/api/v1/price-signal/new-price")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    String response = result.getResponse().getContentAsString();
                    assertThat(response).contains("Invalid metal type: copper");
                });
    }

    @Test
    void shouldRejectInvalidPriceFormat() throws Exception {
        String json = """
                {
                "itemType": "gold",
                "price": "not-a-number"
                }
                """;

        mockMvc.perform(post("/api/v1/price-signal/new-price")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    String response = result.getResponse().getContentAsString();
                    assertThat(response).contains("\"message\":\"Malformed JSON request\"");
                });
    }

    @Test
    void shouldAcceptPriceWithSpaces() throws Exception {
        String json = """
                {
                "itemType": " silver ",
                "price": " 100.50 "
                }
                """;

        mockMvc.perform(post("/api/v1/price-signal/new-price")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectBlankMetalType() throws Exception {
        String json = """
                {
                "itemType": "  ",
                "price": "100.50"
                }
                """;

        mockMvc.perform(post("/api/v1/price-signal/new-price")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    String response = result.getResponse().getContentAsString();
                    assertThat(response).contains("Metal type cannot be empty");
                });
    }

    @Test
    void shouldRejectNegativePrice() throws Exception {
        String json = """
                {
                "itemType": "silver",
                "price": "-10.00"
                }
                """;

        mockMvc.perform(post("/api/v1/price-signal/new-price")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    String response = result.getResponse().getContentAsString();
                    assertThat(response).contains("Price must be positive");
                });
    }
}
