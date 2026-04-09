package com.pm.preciousmetals.infrastructure.web.dto;

import com.pm.preciousmetals.domain.model.EmailRecipient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailRecipientDto(
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    String email
) {
    public static EmailRecipientDto fromDomain(EmailRecipient recipient) {
        return new EmailRecipientDto(recipient.email());
    }

    public EmailRecipient toDomain() {
        return new EmailRecipient(email);
    }
}

