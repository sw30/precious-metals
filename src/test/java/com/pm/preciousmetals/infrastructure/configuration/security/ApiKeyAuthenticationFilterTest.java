package com.pm.preciousmetals.infrastructure.configuration.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.io.IOException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;



class ApiKeyAuthenticationFilterTest {

    private ApiKeyAuthenticationFilter filter;
    private AppSecurityProperties properties;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        properties = new AppSecurityProperties();
        properties.setApiKey("test-key-12345");
        properties.setAdminApiKey("admin-key-67890");
        
        filter = new ApiKeyAuthenticationFilter(properties);
        
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
        
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldAuthenticateAsM2MWithRegularApiKey() throws ServletException, IOException {

        when(request.getHeader("X-API-KEY")).thenReturn("test-key-12345");

        filter.doFilter(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getName()).isEqualTo("m2m-client");
        assertThat(auth.getAuthorities()).extracting("authority").containsExactly("ROLE_M2M");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldAuthenticateAsAdminWithAdminApiKey() throws ServletException, IOException {

        when(request.getHeader("X-API-KEY")).thenReturn("admin-key-67890");

        filter.doFilter(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getName()).isEqualTo("admin-client");
        assertThat(auth.getAuthorities()).extracting("authority").containsExactly("ROLE_ADMIN");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotAuthenticateWithInvalidKey() throws ServletException, IOException {

        when(request.getHeader("X-API-KEY")).thenReturn("invalid-key");

        filter.doFilter(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotAuthenticateWithoutKey() throws ServletException, IOException {

        when(request.getHeader("X-API-KEY")).thenReturn(null);

        filter.doFilter(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull();
        verify(filterChain).doFilter(request, response);
    }
}

