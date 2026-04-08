package com.pm.preciousmetals.domain.port;

import com.pm.preciousmetals.domain.model.EmailSendingRule;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface EmailSendingRuleRepository {
    EmailSendingRule save(EmailSendingRule rule);
    Optional<EmailSendingRule> findById(UUID id);
    List<EmailSendingRule> findAll();
    void deleteById(UUID id);
}
