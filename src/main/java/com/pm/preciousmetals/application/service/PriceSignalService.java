package com.pm.preciousmetals.application.service;

import com.pm.preciousmetals.application.usecase.ProcessPriceSignalUseCase;
import com.pm.preciousmetals.domain.model.EmailTemplate;
import com.pm.preciousmetals.domain.model.PriceSignal;
import com.pm.preciousmetals.domain.port.EmailSender;
import com.pm.preciousmetals.domain.port.EmailTemplateRepository;
import com.pm.preciousmetals.domain.port.PriceSignalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class PriceSignalService implements ProcessPriceSignalUseCase {

    private final PriceSignalRepository signalRepository;
    private final EmailTemplateRepository templateRepository;
    private final EmailSender emailSender;

    @Override
    public PriceSignal processPriceSignal(PriceSignal signal) {
        log.info("Processing price signal: {}", signal);
        PriceSignal savedSignal = signalRepository.save(signal);
        log.info("Signal saved: {}", savedSignal);

        List<EmailTemplate> templates = templateRepository.findAll();

        for (EmailTemplate template : templates) {
            if (template.shouldBeSentFor(savedSignal)) {

                log.info("Template {} matched for signal! Preparing emails.", template.title());
                template.recipients().forEach(recipient -> emailSender.send(
                        recipient.email(),
                        template.title(),
                        template.content()
                ));
            }
        }

        return savedSignal;
    }
}