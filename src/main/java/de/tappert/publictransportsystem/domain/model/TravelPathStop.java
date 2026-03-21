package de.tappert.publictransportsystem.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Represents a stop within a {@link TravelPath} together with its order.
 * <p>
 * The {@code sequenceNumber} defines the position of the stop inside the travel path.
 */
@Entity
@Table(name = "travel_path_stops")
@Getter
@NoArgsConstructor
public class TravelPathStop extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "travel_path_id", nullable = false)
    private TravelPath travelPath;

    @ManyToOne(optional = false)
    @JoinColumn(name = "stop_id", nullable = false)
    private Stop stop;

    @Column(nullable = false)
    private int sequenceNumber;

    public TravelPathStop(TravelPath travelPath, Stop stop, int sequenceNumber) {
        if (travelPath == null) throw new IllegalArgumentException("travelPath must not be null");
        if (stop == null) throw new IllegalArgumentException("stop must not be null");
        if (sequenceNumber < 1) throw new IllegalArgumentException("sequenceNumber must be at least 1");
        this.travelPath = travelPath;
        this.stop = stop;
        this.sequenceNumber = sequenceNumber;
    }
}

