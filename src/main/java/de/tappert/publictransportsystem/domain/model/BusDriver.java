package de.tappert.publictransportsystem.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bus_drivers")
@Getter
@NoArgsConstructor
public class BusDriver extends Person {

    public BusDriver(String firstName, String lastName) {
        super(firstName, lastName);
    }
}
