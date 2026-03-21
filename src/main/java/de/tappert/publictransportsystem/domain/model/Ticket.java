package de.tappert.publictransportsystem.domain.model;

import de.tappert.publictransportsystem.domain.valueobject.Money;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents a ticket for a specific trip execution.
 * <p>
 * A ticket is issued for one traveler and stores the price at the time of purchase.
 */
@Entity
@Table(name = "tickets")
@Getter
@NoArgsConstructor
public class Ticket extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "traveler_id", nullable = false)
    private Person traveler;

    @ManyToOne(optional = false)
    @JoinColumn(name = "trip_execution_id", nullable = false)
    private TripExecution tripExecution;

    @Embedded
    private Money price;

    @Column(nullable = false)
    private LocalDateTime issuedAt;

    public Ticket(Person traveler, TripExecution tripExecution, Money price, LocalDateTime issuedAt) {
        this.traveler = traveler;
        this.tripExecution = tripExecution;
        this.price = price;
        this.issuedAt = issuedAt;
    }
}
