package com.pm.preciousmetals.infrastructure.persistence.repository;

import com.pm.preciousmetals.infrastructure.persistence.entity.EmailTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface EmailTemplateRepository extends JpaRepository<EmailTemplateEntity, UUID> {
}

