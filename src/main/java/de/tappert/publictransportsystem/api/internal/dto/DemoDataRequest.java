package de.tappert.publictransportsystem.api.internal.dto;

import de.tappert.publictransportsystem.domain.enums.Currency;
import de.tappert.publictransportsystem.domain.enums.DayType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record DemoDataRequest(
        @NotBlank
        String tripName,

        @NotNull
        DayType dayType,

        @NotNull
        @DecimalMin(value = "0.0")
        BigDecimal fareAmount,

        @NotNull
        Currency currency,

        @NotNull
        LocalDateTime startExecutionTime,

        @NotNull
        @Min(value = 1)
        Integer numberOfExecutions,

        @NotNull
        @Min(value = 1)
        Integer executionIntervalMinutes,

        @NotNull
        @Min(value = 1)
        Integer busesPerExecution,

        @NotBlank
        String busNumberPrefix,

        @NotEmpty
        List<String> stopNames,

        @NotBlank
        String locationPrefix
) {
}
