package de.tappert.publictransportsystem.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "passengers")
@Getter
@NoArgsConstructor
public class Passenger extends Person {

    public Passenger(String firstName, String lastName) {
        super(firstName, lastName);
    }
}
