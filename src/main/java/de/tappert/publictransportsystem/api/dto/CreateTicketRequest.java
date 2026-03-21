package de.tappert.publictransportsystem.api.dto;

import jakarta.validation.constraints.NotNull;

public record CreateTicketRequest(
        @NotNull(message = "must not be null")
        Long travelerId,

        @NotNull(message = "must not be null")
        Long tripExecutionId
) {
}

