package de.tappert.publictransportsystem.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the concrete execution of a trip at a specific point in time.
 * <p>
 * Each trip execution belongs to one trip and is assigned to at least one bus
 * and at least one bus driver.
 */
@Entity
@Table(name = "trip_executions")
@Getter
@NoArgsConstructor
public class TripExecution extends BaseEntity {

    @ManyToMany
    @JoinTable(
            name = "trip_execution_buses",
            joinColumns = @JoinColumn(name = "trip_execution_id"),
            inverseJoinColumns = @JoinColumn(name = "bus_id")
    )
    private final List<Bus> buses = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "trip_execution_driver",
            joinColumns = @JoinColumn(name = "trip_execution_id"),
            inverseJoinColumns = @JoinColumn(name = "driver_id")
    )
    private final List<BusDriver> busDrivers = new ArrayList<>();

    @ManyToOne(optional = false)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @Column(nullable = false)
    private LocalDateTime executionTime;

    public TripExecution(Trip trip, LocalDateTime executionTime) {
        this.trip = trip;
        this.executionTime = executionTime;
    }

    public void addBus(Bus bus) {
        this.buses.add(bus);
    }

    public void addBusDriver(BusDriver busDriver) {
        this.busDrivers.add(busDriver);
    }

    public void validate() {
        if (buses.isEmpty()) {
            throw new IllegalStateException("A trip execution must have at least one bus");
        }
        if (busDrivers.isEmpty()) {
            throw new IllegalStateException("A trip execution must have at least one bus driver");
        }
    }
}
