package com.pm.preciousmetals.infrastructure.configuration.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.preciousmetals.infrastructure.web.error.ApiError;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoggingAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;
    private final Optional<Tracer> tracer;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {

        String traceId = getTraceId();

        log.warn("Unauthorized request: {} {} from IP: {}. Error: {}. TraceId: {}",
                request.getMethod(),
                request.getRequestURI(),
                request.getRemoteAddr(),
                authException.getMessage(),
                traceId);

        ApiError apiError = new ApiError(
                "UNAUTHORIZED",
                authException.getMessage(),
                request.getRequestURI(),
                traceId,
                LocalDateTime.now()
        );

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), apiError);
    }

    private String getTraceId() {
        try {
            String traceId = tracer
                    .map(Tracer::currentSpan)
                    .map(span -> span.context().traceId())
                    .orElse(null);

            if (traceId == null) {
                traceId = org.slf4j.MDC.get("traceId");
            }

            return traceId != null ? traceId : "no-trace";
        } catch (Exception e) {
            log.error("Error getting traceId: ", e);
            return "error-trace";
        }
    }
}
