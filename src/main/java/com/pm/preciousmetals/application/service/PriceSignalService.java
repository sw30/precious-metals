package com.pm.preciousmetals.application.service;

import com.pm.preciousmetals.application.usecase.ProcessPriceSignalUseCase;
import com.pm.preciousmetals.domain.model.EmailTemplate;
import com.pm.preciousmetals.domain.model.PriceSignal;
import com.pm.preciousmetals.domain.port.EmailSenderPort;
import com.pm.preciousmetals.domain.port.EmailTemplateRepositoryPort;
import com.pm.preciousmetals.domain.port.PriceSignalRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
public class PriceSignalService implements ProcessPriceSignalUseCase {

    private final PriceSignalRepositoryPort signalRepository;
    private final EmailTemplateRepositoryPort templateRepository;
    private final EmailSenderPort emailSenderPort;

    @Override
    public PriceSignal processPriceSignal(PriceSignal signal) {
        log.info("Processing price signal: {}", signal);
        PriceSignal savedSignal = signalRepository.save(signal);
        log.info("Signal saved: {}", savedSignal);

        List<EmailTemplate> templates = templateRepository.findAll();

        for (EmailTemplate template : templates) {
            if (template.shouldBeSentFor(savedSignal)) {

                log.info("Template {} matched for signal! Preparing emails.", template.title());
                template.recipients().forEach(recipient -> emailSenderPort.send(
                        recipient.email(),
                        template.title(),
                        template.content()
                ));
            }
        }

        return savedSignal;
    }
}
