package com.pm.preciousmetals.infrastructure.configuration.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "app.security")
@Getter
@Setter
public class AppSecurityProperties {

    private String apiKey = "test-key-12345";
    private String adminApiKey = "admin-key-67890";
    private String allowedIps = "127.0.0.1,0:0:0:0:0:0:0:1";
    private String protectedPath = "/api/v1/price-signal/**";
    
    private RateLimit rateLimit = new RateLimit();
    private CircuitBreaker circuitBreaker = new CircuitBreaker();

    @Getter
    @Setter
    public static class RateLimit {
        private int requestsPerMinute = 60;
        private Duration refreshPeriod = Duration.ofMinutes(1);
        private Duration timeoutDuration = Duration.ZERO;
    }

    @Getter
    @Setter
    public static class CircuitBreaker {
        private String name = "external-api-signal";
        private float failureRateThreshold = 50.0f;
        private Duration waitDurationInOpenState = Duration.ofMillis(10000);
        private int permittedNumberOfCallsInHalfOpenState = 3;
        private int slidingWindowSize = 10;
        private int minimumNumberOfCalls = 5;
    }
}
