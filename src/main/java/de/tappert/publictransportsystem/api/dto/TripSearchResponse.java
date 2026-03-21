package de.tappert.publictransportsystem.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record TripSearchResponse(
        Long tripExecutionId,
        LocalDateTime executionTime,
        String tripName,
        BigDecimal ticketPrice,
        String currency,
        List<TravelPathResponse> travelPaths
) {
}
