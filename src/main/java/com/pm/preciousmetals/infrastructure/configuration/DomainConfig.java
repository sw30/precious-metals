package com.pm.preciousmetals.infrastructure.configuration;

import com.pm.preciousmetals.application.service.EmailTemplateService;
import com.pm.preciousmetals.application.service.PriceSignalService;
import com.pm.preciousmetals.application.usecase.ManageEmailTemplateUseCase;
import com.pm.preciousmetals.application.usecase.ProcessPriceSignalUseCase;
import com.pm.preciousmetals.domain.port.EmailTemplateRepository;
import com.pm.preciousmetals.domain.port.PriceSignalRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {

    @Bean
    public ProcessPriceSignalUseCase processPriceSignalUseCase(PriceSignalRepository priceSignalRepository) {
        return new PriceSignalService(priceSignalRepository);
    }

    @Bean
    public ManageEmailTemplateUseCase manageEmailTemplateUseCase(EmailTemplateRepository emailTemplateRepository) {
        return new EmailTemplateService(emailTemplateRepository);
    }
}
