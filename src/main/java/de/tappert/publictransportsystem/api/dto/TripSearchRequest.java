package de.tappert.publictransportsystem.api.dto;

import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TripSearchRequest(
        LocalDateTime from,
        LocalDateTime until,
        String location,

        @PositiveOrZero(message = "must be greater than or equal to 0")
        BigDecimal maxPrice
) {
}
