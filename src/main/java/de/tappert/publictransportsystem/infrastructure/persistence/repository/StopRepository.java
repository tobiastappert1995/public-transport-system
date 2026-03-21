package de.tappert.publictransportsystem.infrastructure.persistence.repository;

import de.tappert.publictransportsystem.domain.model.Stop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StopRepository extends JpaRepository<Stop, Long> {
}
