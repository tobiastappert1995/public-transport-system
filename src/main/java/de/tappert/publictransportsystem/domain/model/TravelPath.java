package de.tappert.publictransportsystem.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Represents an ordered path of stops for a trip.
 * <p>
 * A travel path consists of multiple stops. The sequence number defines
 * the order of the stops in the route.
 */
@Entity
@Table(name = "travel_paths")
@Getter
@NoArgsConstructor
public class TravelPath extends BaseEntity {

    @OneToMany(mappedBy = "travelPath", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sequenceNumber ASC")
    private final List<TravelPathStop> travelPathStops = new ArrayList<>();

    @Column(nullable = false)
    private String name;

    public TravelPath(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        this.name = name;
    }

    public List<TravelPathStop> getStopsInOrder() {
        return travelPathStops.stream()
                .sorted(Comparator.comparingInt(TravelPathStop::getSequenceNumber))
                .toList();
    }

    public Optional<Stop> getFirstStop() {
        return getStopsInOrder().stream()
                .findFirst()
                .map(TravelPathStop::getStop);
    }

    public Optional<Stop> getLastStop() {
        List<TravelPathStop> orderedStops = getStopsInOrder();
        if (orderedStops.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(orderedStops.getLast().getStop());
    }

    public void addStop(Stop stop, int sequenceNumber) {
        if (stop == null) {
            throw new IllegalArgumentException("stop must not be null");
        }
        if (sequenceNumber < 1) {
            throw new IllegalArgumentException("sequenceNumber must be at least 1");
        }
        travelPathStops.add(new TravelPathStop(this, stop, sequenceNumber));
    }
}
