package com.pm.preciousmetals.infrastructure.persistence.repository;

import com.pm.preciousmetals.infrastructure.persistence.entity.EmailSendingRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface EmailSendingRuleRepository extends JpaRepository<EmailSendingRuleEntity, UUID> {
}
