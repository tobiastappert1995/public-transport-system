package de.tappert.publictransportsystem.infrastructure.persistence.repository;

import de.tappert.publictransportsystem.domain.model.BusDriver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusDriverRepository extends JpaRepository<BusDriver, Long> {
}
