package de.tappert.publictransportsystem.api.internal.dto;

public record DemoDataResponse(
        String message,
        Long tripId,
        Integer createdStops,
        Integer createdExecutions
) {
}

