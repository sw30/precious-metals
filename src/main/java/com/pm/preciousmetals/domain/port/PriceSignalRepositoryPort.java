package com.pm.preciousmetals.domain.port;

import com.pm.preciousmetals.domain.model.PriceSignal;

public interface PriceSignalRepositoryPort {
    PriceSignal save(PriceSignal priceSignal);
}

