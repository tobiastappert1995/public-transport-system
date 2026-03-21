package de.tappert.publictransportsystem.infrastructure.persistence.repository;

import de.tappert.publictransportsystem.domain.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {
}

