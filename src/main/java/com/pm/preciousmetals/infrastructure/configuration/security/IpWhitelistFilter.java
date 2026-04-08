package com.pm.preciousmetals.infrastructure.configuration.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.preciousmetals.infrastructure.web.error.ApiError;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class IpWhitelistFilter extends OncePerRequestFilter {

    private final List<IpAddressMatcher> ipAddressMatchers;
    private final ObjectMapper objectMapper;
    private final Optional<Tracer> tracer;
    private final AppSecurityProperties properties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();

        if (pathMatcher.match(properties.getProtectedPath(), requestUri)) {
            String clientIp = request.getRemoteAddr();
            
            boolean isAllowed = ipAddressMatchers.stream()
                    .anyMatch(matcher -> matcher.matches(clientIp));

            if (!isAllowed) {
                String traceId = getTraceId();
                log.warn("Access denied for IP: {} to path: {}. TraceId: {}", clientIp, requestUri, traceId);

                ApiError apiError = new ApiError(
                        "FORBIDDEN_NETWORK",
                        "Access from your network is restricted",
                        requestUri,
                        traceId,
                        LocalDateTime.now()
                );

                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                objectMapper.writeValue(response.getWriter(), apiError);
                return;
            }
        }

        filterChain.doFilter(request, response);
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
