package com.pm.preciousmetals.application.usecase;

import com.pm.preciousmetals.domain.model.PriceSignal;

public interface ProcessPriceSignalUseCase {
    PriceSignal processPriceSignal(PriceSignal signal);
}
