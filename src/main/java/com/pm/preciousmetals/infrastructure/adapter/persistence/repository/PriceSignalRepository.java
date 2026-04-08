package com.pm.preciousmetals.infrastructure.adapter.persistence.repository;

import com.pm.preciousmetals.infrastructure.adapter.persistence.entity.PriceSignalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PriceSignalRepository extends JpaRepository<PriceSignalEntity, UUID> {
}
