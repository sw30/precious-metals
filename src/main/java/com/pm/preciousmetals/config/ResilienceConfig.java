package com.pm.preciousmetals.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.util.matcher.IpAddressMatcher;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class ResilienceConfig {

    private final AppSecurityProperties properties;

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        AppSecurityProperties.CircuitBreaker cb = properties.getCircuitBreaker();
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(cb.getFailureRateThreshold())
                .waitDurationInOpenState(cb.getWaitDurationInOpenState())
                .permittedNumberOfCallsInHalfOpenState(cb.getPermittedNumberOfCallsInHalfOpenState())
                .slidingWindowSize(cb.getSlidingWindowSize())
                .minimumNumberOfCalls(cb.getMinimumNumberOfCalls())
                .build();
        return CircuitBreakerRegistry.of(config);
    }

    @Bean
    public List<IpAddressMatcher> ipAddressMatchers() {
        return Arrays.stream(properties.getAllowedIps().split(","))
                .map(String::trim)
                .map(IpAddressMatcher::new)
                .collect(Collectors.toList());
    }

    @Bean
    public RateLimiterRegistry rateLimiterRegistry() {
        AppSecurityProperties.RateLimit rl = properties.getRateLimit();
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitRefreshPeriod(rl.getRefreshPeriod())
                .limitForPeriod(rl.getRequestsPerMinute())
                .timeoutDuration(rl.getTimeoutDuration())
                .build();

        return RateLimiterRegistry.of(config);
    }
}
