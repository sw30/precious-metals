package com.pm.preciousmetals.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PriceSignalRepository extends JpaRepository<PriceSignalEntity, UUID> {
}
