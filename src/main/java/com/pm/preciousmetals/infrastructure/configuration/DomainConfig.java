package com.pm.preciousmetals.infrastructure.configuration;

import com.pm.preciousmetals.application.service.EmailTemplateService;
import com.pm.preciousmetals.application.service.PriceSignalService;
import com.pm.preciousmetals.application.usecase.ManageEmailTemplateUseCase;
import com.pm.preciousmetals.application.usecase.ProcessPriceSignalUseCase;
import com.pm.preciousmetals.domain.port.EmailSenderPort;
import com.pm.preciousmetals.domain.port.EmailTemplateRepositoryPort;
import com.pm.preciousmetals.domain.port.PriceSignalRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {

    @Bean
    public ProcessPriceSignalUseCase processPriceSignalUseCase(
            PriceSignalRepositoryPort priceSignalRepositoryPort,
            EmailTemplateRepositoryPort emailTemplateRepositoryPort,
            EmailSenderPort emailSenderPort) {
        return new PriceSignalService(priceSignalRepositoryPort, emailTemplateRepositoryPort, emailSenderPort);
    }

    @Bean
    public ManageEmailTemplateUseCase manageEmailTemplateUseCase(EmailTemplateRepositoryPort emailTemplateRepositoryPort) {
        return new EmailTemplateService(emailTemplateRepositoryPort);
    }
}

