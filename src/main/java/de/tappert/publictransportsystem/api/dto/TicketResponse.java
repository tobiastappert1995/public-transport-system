package de.tappert.publictransportsystem.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TicketResponse(
        Long ticketId,
        Long travelerId,
        Long tripExecutionId,
        BigDecimal price,
        String currency,
        LocalDateTime issuedAt
) {
}

