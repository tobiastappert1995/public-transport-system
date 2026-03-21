package de.tappert.publictransportsystem.domain.model;

import de.tappert.publictransportsystem.domain.enums.DayType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a reusable trip definition.
 * <p>
 * A trip combines one or more travel paths, a fare and a day type. Concrete
 * executions of a trip are modeled by {@link TripExecution}.
 */
@Entity
@Table(name = "trips")
@Getter
@NoArgsConstructor
public class Trip extends BaseEntity {

    @ManyToMany
    @JoinTable(
            name = "trip_travel_paths",
            joinColumns = @JoinColumn(name = "trip_id"),
            inverseJoinColumns = @JoinColumn(name = "travel_path_id")
    )
    private final List<TravelPath> travelPaths = new ArrayList<>();

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayType dayType;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fare_id", nullable = false)
    private Fare fare;

    public Trip(String name, DayType dayType, Fare fare) {
        this.name = name;
        this.dayType = dayType;
        this.fare = fare;
    }

    public void addTravelPath(TravelPath travelPath) {
        travelPaths.add(travelPath);
    }

    public void validate() {
        if (name == null || name.isBlank()) throw new IllegalStateException("A trip must have a name");
        if (dayType == null) throw new IllegalStateException("A trip must have a day type");
        if (fare == null) throw new IllegalStateException("A trip must have a fare");
        if (travelPaths.isEmpty()) {
            throw new IllegalStateException("A trip must have at least one travel path");
        }
    }
}
