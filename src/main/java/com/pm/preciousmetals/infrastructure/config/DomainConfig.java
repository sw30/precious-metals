package com.pm.preciousmetals.infrastructure.config;

import com.pm.preciousmetals.application.service.PriceSignalService;
import com.pm.preciousmetals.application.usecase.ProcessPriceSignalUseCase;
import com.pm.preciousmetals.domain.port.PriceSignalRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {

    @Bean
    public ProcessPriceSignalUseCase processPriceSignalUseCase(PriceSignalRepository priceSignalRepository) {
        return new PriceSignalService(priceSignalRepository);
    }
}
