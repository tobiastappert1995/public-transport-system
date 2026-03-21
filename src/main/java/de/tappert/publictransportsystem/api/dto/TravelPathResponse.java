package de.tappert.publictransportsystem.api.dto;

import java.util.List;

public record TravelPathResponse(
        String name,
        String departureStop,
        String destinationStop,
        List<TripStopResponse> stops
) {
}
