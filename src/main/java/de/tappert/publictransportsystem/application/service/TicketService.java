package de.tappert.publictransportsystem.application.service;

import de.tappert.publictransportsystem.api.dto.CreateTicketRequest;
import de.tappert.publictransportsystem.api.dto.TicketResponse;
import de.tappert.publictransportsystem.application.exception.ResourceNotFoundException;
import de.tappert.publictransportsystem.domain.enums.DayType;
import de.tappert.publictransportsystem.domain.model.Person;
import de.tappert.publictransportsystem.domain.model.Ticket;
import de.tappert.publictransportsystem.domain.model.TripExecution;
import de.tappert.publictransportsystem.domain.strategy.TicketPricingStrategy;
import de.tappert.publictransportsystem.domain.valueobject.Money;
import de.tappert.publictransportsystem.infrastructure.persistence.repository.PersonRepository;
import de.tappert.publictransportsystem.infrastructure.persistence.repository.TicketRepository;
import de.tappert.publictransportsystem.infrastructure.persistence.repository.TripExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Service for creating tickets and calculating their final price.
 * <p>
 * The final price is determined by the pricing strategy for the day type of
 * the related trip execution.
 */
@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final PersonRepository personRepository;
    private final TripExecutionRepository tripExecutionRepository;
    private final Map<DayType, TicketPricingStrategy> ticketPricingStrategies;

    /**
     * Creates a ticket for the given traveler and trip execution.
     */
    @Transactional
    public TicketResponse createTicket(CreateTicketRequest request) {
        Person traveler = personRepository.findById(request.travelerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Person not found with id: " + request.travelerId()
                ));


        TripExecution tripExecution = tripExecutionRepository.findById(request.tripExecutionId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "TripExecution not found with id: " + request.tripExecutionId()
                ));

        Ticket ticket = ticketRepository.save(new Ticket(
                traveler,
                tripExecution,
                calculateTicketPrice(tripExecution),
                LocalDateTime.now()
        ));

        return new TicketResponse(
                ticket.getId(),
                ticket.getTraveler().getId(),
                ticket.getTripExecution().getId(),
                ticket.getPrice().getAmount(),
                ticket.getPrice().getCurrency().name(),
                ticket.getIssuedAt()
        );
    }

    private Money calculateTicketPrice(TripExecution tripExecution) {
        DayType dayType = tripExecution.getTrip().getDayType();
        TicketPricingStrategy strategy = ticketPricingStrategies.get(dayType);

        if (strategy == null) {
            throw new IllegalStateException(
                    String.format("No ticket pricing strategy configured for day type: %s", dayType)
            );
        }

        return strategy.calculatePrice(tripExecution);
    }
}