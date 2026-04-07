package com.pm.preciousmetals.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.web.util.matcher.IpAddressMatcher;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class IpWhitelistFilterTest {

    private IpWhitelistFilter ipWhitelistFilter;
    private ObjectMapper objectMapper;
    private AppSecurityProperties properties;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        objectMapper = mock(ObjectMapper.class);
        Tracer tracer = mock(Tracer.class);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
        properties = new AppSecurityProperties();
        properties.setProtectedPath("/api/v1/price-signal/**");

        List<IpAddressMatcher> matchers = List.of(
                new IpAddressMatcher("127.0.0.1"),
                new IpAddressMatcher("192.168.1.0/24")
        );

        ipWhitelistFilter = new IpWhitelistFilter(matchers, objectMapper, Optional.of(tracer), properties);
    }

    @Test
    void shouldAllowAllowedIp() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/v1/price-signal/new-price");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        ipWhitelistFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(objectMapper);
    }

    @Test
    void shouldAllowIpInRange() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/v1/price-signal/new-price");
        when(request.getRemoteAddr()).thenReturn("192.168.1.50");

        ipWhitelistFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldDenyUnallowedIp() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/v1/price-signal/new-price");
        when(request.getRemoteAddr()).thenReturn("10.0.0.1");
        java.io.PrintWriter writer = mock(java.io.PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        ipWhitelistFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(objectMapper).writeValue(eq(writer), any());
        verifyNoInteractions(filterChain);
    }

    @Test
    void shouldAllowAnyIpForNonProtectedPath() throws Exception {
        when(request.getRequestURI()).thenReturn("/actuator/health");
        when(request.getRemoteAddr()).thenReturn("10.0.0.1");

        ipWhitelistFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }
}
