package com.pm.preciousmetals.infrastructure.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface JpaPriceSignalRepository extends JpaRepository<PriceSignalEntity, UUID> {
}
