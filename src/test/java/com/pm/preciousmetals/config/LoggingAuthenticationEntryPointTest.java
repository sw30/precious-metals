package com.pm.preciousmetals.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class LoggingAuthenticationEntryPointTest {

    private LoggingAuthenticationEntryPoint entryPoint;
    private Tracer tracer;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private AuthenticationException authException;
    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        tracer = mock(Tracer.class);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        authException = mock(AuthenticationException.class);
        
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        
        entryPoint = new LoggingAuthenticationEntryPoint(objectMapper, Optional.of(tracer));
    }

    @Test
    void shouldReturnApiErrorOnUnauthorized() throws Exception {
        // Given
        when(request.getRequestURI()).thenReturn("/api/test");
        when(authException.getMessage()).thenReturn("Full authentication is required");
        
        // When
        entryPoint.commence(request, response, authException);
        
        // Then
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        String jsonResponse = responseWriter.toString();
        assertThat(jsonResponse).contains("\"errorCode\":\"UNAUTHORIZED\"");
        assertThat(jsonResponse).contains("\"message\":\"Full authentication is required\"");
        assertThat(jsonResponse).contains("\"path\":\"/api/test\"");
        assertThat(jsonResponse).contains("\"timestamp\"");
    }
}
