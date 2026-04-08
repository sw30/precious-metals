package com.pm.preciousmetals.infrastructure.adapter.persistence.repository_adapters;

import com.pm.preciousmetals.domain.model.PriceSignal;
import com.pm.preciousmetals.infrastructure.adapter.persistence.entity.PriceSignalEntity;
import com.pm.preciousmetals.infrastructure.adapter.persistence.repository.PriceSignalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PriceSignalRepositoryAdapter implements com.pm.preciousmetals.domain.port.PriceSignalRepository {

    private final PriceSignalRepository jpaRepository;

    @Override
    public PriceSignal save(PriceSignal priceSignal) {
        PriceSignalEntity entity = new PriceSignalEntity();
        entity.setPrice(priceSignal.price().value());
        entity.setMetalType(priceSignal.metalType());

        jpaRepository.save(entity);
        return priceSignal;
    }
}
