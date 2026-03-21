package de.tappert.publictransportsystem.api.dto;

public record TripStopResponse(
        int order,
        String stopName,
        String location
) {
}


