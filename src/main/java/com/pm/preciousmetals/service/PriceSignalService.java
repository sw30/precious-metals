package com.pm.preciousmetals.service;

import com.pm.preciousmetals.domain.PriceSignal;
import com.pm.preciousmetals.domain.PriceSignalEntity;
import com.pm.preciousmetals.domain.PriceSignalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PriceSignalService {

    private final PriceSignalRepository repository;

    @Transactional
    public PriceSignal processPriceSignal(PriceSignal signal) {
        log.info("Processing price signal: {}", signal);

        PriceSignalEntity entity = new PriceSignalEntity();
        entity.setPrice(signal.price().value());
        entity.setMetalType(signal.metalType());

        repository.save(entity);
        log.info("Signal saved to database: {}", signal);
        return signal;
    }
}
