package de.tappert.publictransportsystem.infrastructure.persistence.repository;

import de.tappert.publictransportsystem.domain.model.TravelPath;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravelPathRepository extends JpaRepository<TravelPath, Long> {
}
