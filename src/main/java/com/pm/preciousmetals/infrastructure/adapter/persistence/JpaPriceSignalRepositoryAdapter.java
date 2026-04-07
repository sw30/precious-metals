package com.pm.preciousmetals.infrastructure.adapter.persistence;

import com.pm.preciousmetals.domain.model.PriceSignal;
import com.pm.preciousmetals.domain.port.PriceSignalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaPriceSignalRepositoryAdapter implements PriceSignalRepository {

    private final JpaPriceSignalRepository jpaRepository;

    @Override
    public PriceSignal save(PriceSignal priceSignal) {
        PriceSignalEntity entity = new PriceSignalEntity();
        entity.setPrice(priceSignal.price().value());
        entity.setMetalType(priceSignal.metalType());

        jpaRepository.save(entity);
        return priceSignal;
    }
}
