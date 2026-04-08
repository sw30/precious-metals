package com.pm.preciousmetals.infrastructure.configuration.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.preciousmetals.infrastructure.web.error.ApiError;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class RateLimitingFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final Optional<Tracer> tracer;
    private final RateLimiterRegistry rateLimiterRegistry;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final AppSecurityProperties properties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();

        if (pathMatcher.match(properties.getProtectedPath(), requestUri)) {
            String clientIp = request.getRemoteAddr();
            RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter(clientIp);
            CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(properties.getCircuitBreaker().getName());

            try {
                circuitBreaker.executeCheckedSupplier(() -> {
                    if (!rateLimiter.acquirePermission()) {
                        throw new RateLimitExceededException(clientIp);
                    }
                    filterChain.doFilter(request, response);
                    return null;
                });
                return; // filterChain.doFilter already called
            } catch (RateLimitExceededException e) {
                handleError(requestUri, clientIp, response, "TOO_MANY_REQUESTS", "Rate limit exceeded. Please try again later.", HttpStatus.TOO_MANY_REQUESTS);
                return;
            } catch (CallNotPermittedException e) {
                handleError(requestUri, clientIp, response, "SERVICE_UNAVAILABLE", "Service is temporarily unavailable due to high error rate.", HttpStatus.SERVICE_UNAVAILABLE);
                return;
            } catch (Throwable e) {
                if (e instanceof ServletException) throw (ServletException) e;
                if (e instanceof IOException) throw (IOException) e;
                throw new ServletException(e);
            }
        }

        filterChain.doFilter(request, response);
    }

    private void handleError(String requestUri, String clientIp, HttpServletResponse response, String errorCode, String message, HttpStatus status) throws IOException {
        String traceId = getTraceId();
        log.warn("{} for IP: {}. TraceId: {}", errorCode, clientIp, traceId);

        ApiError apiError = new ApiError(
                errorCode,
                message,
                requestUri,
                traceId,
                LocalDateTime.now()
        );

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), apiError);
    }

    private static class RateLimitExceededException extends RuntimeException {
        public RateLimitExceededException(String ip) {
            super("Rate limit exceeded for IP: " + ip);
        }
    }

    private String getTraceId() {
        return tracer
                .map(Tracer::currentSpan)
                .map(span -> span.context().traceId())
                .orElseGet(() -> {
                    String mdcTraceId = org.slf4j.MDC.get("traceId");
                    return mdcTraceId != null ? mdcTraceId : "no-trace";
                });
    }
}
