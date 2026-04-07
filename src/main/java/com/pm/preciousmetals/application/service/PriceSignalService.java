package com.pm.preciousmetals.application.service;

import com.pm.preciousmetals.application.usecase.ProcessPriceSignalUseCase;
import com.pm.preciousmetals.domain.model.PriceSignal;
import com.pm.preciousmetals.domain.port.PriceSignalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class PriceSignalService implements ProcessPriceSignalUseCase {

    private final PriceSignalRepository repository;

    @Override
    public PriceSignal processPriceSignal(PriceSignal signal) {
        log.info("Processing price signal: {}", signal);
        PriceSignal savedSignal = repository.save(signal);
        log.info("Signal saved: {}", savedSignal);
        return savedSignal;
    }
}
