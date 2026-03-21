package de.tappert.publictransportsystem.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Represents a physical bus stop with a start date and an optional end date.
 */
@Entity
@Table(name = "stops")
@Getter
@NoArgsConstructor
public class Stop extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private LocalDate validFrom;

    private LocalDate validTo;

    public Stop(String name, String location, LocalDate validFrom, LocalDate validTo) {
        this.name = name;
        this.location = location;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }
}
