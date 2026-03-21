package de.tappert.publictransportsystem.domain.model;

import de.tappert.publictransportsystem.domain.enums.EngineType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "buses")
@Getter
@NoArgsConstructor
public class Bus extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String busNumber;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private double lengthMeters;

    @Column(nullable = false)
    private int maxCapacity;

    @Column(nullable = false)
    private double maxSpeed;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EngineType engineType;

    @Column(nullable = false)
    private LocalDate inServiceSince;

    public Bus(String busNumber, String name, String brand, String model, double lengthMeters, int maxCapacity, double maxSpeed, EngineType engineType, LocalDate inServiceSince) {
        this.busNumber = busNumber;
        this.name = name;
        this.brand = brand;
        this.model = model;
        this.lengthMeters = lengthMeters;
        this.maxCapacity = maxCapacity;
        this.maxSpeed = maxSpeed;
        this.engineType = engineType;
        this.inServiceSince = inServiceSince;
    }
}
