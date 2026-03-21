package de.tappert.publictransportsystem.infrastructure.persistence.repository;

import de.tappert.publictransportsystem.domain.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRepository extends JpaRepository<Trip, Long> {
}
