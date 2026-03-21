package de.tappert.publictransportsystem.application.service;

import de.tappert.publictransportsystem.api.dto.TripSearchRequest;
import de.tappert.publictransportsystem.api.dto.TripSearchResponse;
import de.tappert.publictransportsystem.api.mapper.TripSearchMapper;
import de.tappert.publictransportsystem.domain.enums.DayType;
import de.tappert.publictransportsystem.domain.model.TripExecution;
import de.tappert.publictransportsystem.domain.strategy.TicketPricingStrategy;
import de.tappert.publictransportsystem.domain.valueobject.Money;
import de.tappert.publictransportsystem.infrastructure.persistence.repository.TripExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Provides functionality to search for trip executions by time range,
 * location, and maximum price.
 */
@Service
@RequiredArgsConstructor
public class TripSearchService {

    private static final LocalDateTime DEFAULT_FROM = LocalDateTime.of(2000, 1, 1, 0, 0);
    private static final LocalDateTime DEFAULT_UNTIL = LocalDateTime.of(2100, 1, 1, 0, 0);

    private final TripExecutionRepository tripExecutionRepository;
    private final Map<DayType, TicketPricingStrategy> ticketPricingStrategies;

    private static void validateRange(TripSearchRequest request) {
        if (request.from() != null
                && request.until() != null
                && request.from().isAfter(request.until())) {
            throw new IllegalArgumentException("from must not be after until");
        }
    }

    @Transactional(readOnly = true)
    public List<TripSearchResponse> search(TripSearchRequest request) {
        validateRange(request);

        LocalDateTime from = request.from() != null ? request.from() : DEFAULT_FROM;
        LocalDateTime until = request.until() != null ? request.until() : DEFAULT_UNTIL;

        List<TripExecution> executions = findExecutions(request, from, until);
        List<TripSearchResponse> responses = new ArrayList<>();

        for (TripExecution execution : executions) {
            Money ticketPrice = calculateTicketPrice(execution);

            if (!isAboveMaxPrice(ticketPrice, request.maxPrice())) {
                responses.add(TripSearchMapper.toResponse(execution, ticketPrice));
            }
        }

        return responses;
    }

    private List<TripExecution> findExecutions(
            TripSearchRequest request,
            LocalDateTime from,
            LocalDateTime until
    ) {
        String location = request.location();

        if (location != null && !location.isBlank()) {
            return tripExecutionRepository.findByLocationAndTimeRange(location, from, until);
        }

        return tripExecutionRepository.findByTimeRange(from, until);
    }

    private Money calculateTicketPrice(TripExecution tripExecution) {
        DayType dayType = tripExecution.getTrip().getDayType();
        TicketPricingStrategy pricingStrategy = ticketPricingStrategies.get(dayType);

        if (pricingStrategy == null) {
            throw new IllegalStateException("No pricing strategy configured for day type: " + dayType);
        }

        return pricingStrategy.calculatePrice(tripExecution);
    }

    private boolean isAboveMaxPrice(Money ticketPrice, BigDecimal maxPrice) {
        return maxPrice != null && ticketPrice.getAmount().compareTo(maxPrice) > 0;
    }
}
