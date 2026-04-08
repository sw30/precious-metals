package com.pm.preciousmetals.domain.port;

import com.pm.preciousmetals.domain.model.EmailTemplate;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface EmailTemplateRepository {
    EmailTemplate save(EmailTemplate template);
    Optional<EmailTemplate> findById(UUID id);
    List<EmailTemplate> findAll();
    void deleteById(UUID id);
}
