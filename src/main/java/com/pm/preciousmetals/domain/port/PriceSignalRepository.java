package com.pm.preciousmetals.domain.port;

import com.pm.preciousmetals.domain.model.PriceSignal;

public interface PriceSignalRepository {
    PriceSignal save(PriceSignal priceSignal);
}
