package com.pm.preciousmetals.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Precious Metals API",
        version = "0.0.1",
        description = "API for tracking and processing precious metals price signals"
    ),
    security = @SecurityRequirement(name = "X-API-KEY")
)
@SecurityScheme(
    name = "X-API-KEY",
    type = SecuritySchemeType.APIKEY,
    in = SecuritySchemeIn.HEADER,
    paramName = "X-API-KEY",
    description = "Enter your API Key to access authenticated endpoints"
)
public class OpenApiConfig {
}
