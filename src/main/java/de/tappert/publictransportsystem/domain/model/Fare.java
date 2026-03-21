package de.tappert.publictransportsystem.domain.model;

import de.tappert.publictransportsystem.domain.valueobject.Money;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Represents a fare with a name and a price.
 */
@Entity
@Table(name = "fares")
@Getter
@NoArgsConstructor
public class Fare extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Embedded
    private Money price;

    public Fare(String name, Money price) {
        this.name = name;
        this.price = price;
    }
}
