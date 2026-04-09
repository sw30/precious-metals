package com.pm.preciousmetals.domain.model.rules;

public enum Operator {
    IS_EQUAL,
    IS_NOT_EQUAL,

    GREATER_THAN_EQUAL,
    GREATER_THAN,

    LESS_THAN,
    LESS_THAN_EQUAL;

    public boolean isGreater() {
        return this == GREATER_THAN || this == GREATER_THAN_EQUAL;
    }

    public boolean isLess() {
        return this == LESS_THAN || this == LESS_THAN_EQUAL;
    }
}

