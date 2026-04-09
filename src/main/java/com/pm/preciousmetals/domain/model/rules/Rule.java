package com.pm.preciousmetals.domain.model.rules;

import com.pm.preciousmetals.domain.model.PriceSignal;

public interface Rule {
    boolean matches(PriceSignal signal);

    default boolean isExclusive(Rule other) {
        return false;
    }
}

