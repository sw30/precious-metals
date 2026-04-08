package com.pm.preciousmetals.infrastructure.web.dto;

import com.pm.preciousmetals.domain.model.EmailRecipient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record EmailRecipientDto(
    UUID id,
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    String email
) {
    public static EmailRecipientDto fromDomain(EmailRecipient recipient) {
        return new EmailRecipientDto(null, recipient.email());
    }

    public EmailRecipient toDomain() {
        return new EmailRecipient(email);
    }
}
