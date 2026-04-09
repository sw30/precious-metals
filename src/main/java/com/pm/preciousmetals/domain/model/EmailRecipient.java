package com.pm.preciousmetals.domain.model;

import java.util.Objects;

public record EmailRecipient(String email) {
    public EmailRecipient {
        Objects.requireNonNull(email, "Email cannot be null");
        if (email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be blank");
        }
    }
}

