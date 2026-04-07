package com.pm.preciousmetals.infrastructure.configuration.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RateLimitingFilterTest {

    private RateLimitingFilter rateLimitingFilter;
    private ObjectMapper objectMapper;
    private Tracer tracer;
    private AppSecurityProperties properties;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;
    private RateLimiterRegistry rateLimiterRegistry;
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @BeforeEach
    void setUp() {
        objectMapper = mock(ObjectMapper.class);
        tracer = mock(Tracer.class);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);

        properties = new AppSecurityProperties();
        properties.setProtectedPath("/api/v1/price-signal/**");
        properties.getCircuitBreaker().setName("external-api-signal");

        RateLimiterConfig rateLimiterConfig = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .limitForPeriod(2)
                .timeoutDuration(Duration.ZERO)
                .build();
        rateLimiterRegistry = RateLimiterRegistry.of(rateLimiterConfig);

        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .slidingWindowSize(10)
                .build();
        circuitBreakerRegistry = CircuitBreakerRegistry.of(circuitBreakerConfig);

        rateLimitingFilter = new RateLimitingFilter(objectMapper, Optional.of(tracer), rateLimiterRegistry, circuitBreakerRegistry, properties);
    }

    @Test
    void shouldAllowWithinLimit() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/v1/price-signal/new-price");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        rateLimitingFilter.doFilterInternal(request, response, filterChain);
        rateLimitingFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(2)).doFilter(request, response);
    }

    @Test
    void shouldDenyExceedingLimit() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/v1/price-signal/new-price");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        java.io.PrintWriter writer = mock(java.io.PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        rateLimitingFilter.doFilterInternal(request, response, filterChain);
        rateLimitingFilter.doFilterInternal(request, response, filterChain);
        rateLimitingFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(2)).doFilter(request, response);
        verify(response).setStatus(429); // Too many requests
        verify(objectMapper).writeValue(eq(writer), any());
    }
}
