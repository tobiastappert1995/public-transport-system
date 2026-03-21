package de.tappert.publictransportsystem.integration;

import de.tappert.publictransportsystem.api.dto.CreateTicketRequest;
import de.tappert.publictransportsystem.api.dto.TicketResponse;
import de.tappert.publictransportsystem.application.exception.ResourceNotFoundException;
import de.tappert.publictransportsystem.application.service.TicketService;
import de.tappert.publictransportsystem.domain.enums.DayType;
import de.tappert.publictransportsystem.domain.model.BusDriver;
import de.tappert.publictransportsystem.domain.model.Passenger;
import de.tappert.publictransportsystem.domain.model.TripExecution;
import de.tappert.publictransportsystem.integration.setup.TicketTestSetup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class TicketServiceIntegrationTest {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketTestSetup ticketTestSetup;

    @BeforeEach
    void setUp() {
        ticketTestSetup.reset();
    }

    @AfterEach
    void tearDown() {
        ticketTestSetup.reset();
    }

    @Test
    void shouldCreateTicketForPassenger() {
        Passenger passenger = ticketTestSetup.createPassenger("Anna", "Musterfrau");

        TripExecution tripExecution = ticketTestSetup.createTripExecution(
                "Weekday Line 1",
                DayType.WEEKDAY,
                new BigDecimal("3.20"),
                LocalDateTime.of(2025, 9, 10, 9, 0)
        );

        TicketResponse ticket = ticketService.createTicket(
                new CreateTicketRequest(passenger.getId(), tripExecution.getId())
        );

        assertThat(ticket.ticketId()).isNotNull();
        assertThat(ticket.travelerId()).isEqualTo(passenger.getId());
        assertThat(ticket.tripExecutionId()).isEqualTo(tripExecution.getId());
        assertThat(ticket.price()).isEqualByComparingTo("3.20");
        assertThat(ticket.currency()).isEqualTo("EUR");
        assertThat(ticket.issuedAt()).isNotNull();
    }

    @Test
    void shouldCreateTicketForBusDriverAsTraveler() {
        BusDriver busDriver = ticketTestSetup.createBusDriver("Max", "Mustermann");

        TripExecution tripExecution = ticketTestSetup.createTripExecution(
                "Weekday Line 2",
                DayType.WEEKDAY,
                new BigDecimal("3.20"),
                LocalDateTime.of(2025, 9, 10, 10, 0)
        );

        TicketResponse ticket = ticketService.createTicket(
                new CreateTicketRequest(busDriver.getId(), tripExecution.getId())
        );

        assertThat(ticket.ticketId()).isNotNull();
        assertThat(ticket.travelerId()).isEqualTo(busDriver.getId());
        assertThat(ticket.tripExecutionId()).isEqualTo(tripExecution.getId());
        assertThat(ticket.price()).isEqualByComparingTo("3.20");
        assertThat(ticket.currency()).isEqualTo("EUR");
        assertThat(ticket.issuedAt()).isNotNull();
    }

    @Test
    void shouldApplyWeekendPricingStrategy() {
        Passenger passenger = ticketTestSetup.createPassenger("Lisa", "Beispiel");

        TripExecution tripExecution = ticketTestSetup.createTripExecution(
                "Weekend Line 7",
                DayType.WEEKEND,
                new BigDecimal("4.00"),
                LocalDateTime.of(2025, 9, 10, 9, 30)
        );

        TicketResponse ticket = ticketService.createTicket(
                new CreateTicketRequest(passenger.getId(), tripExecution.getId())
        );

        assertThat(ticket.price()).isEqualByComparingTo("4.20");
        assertThat(ticket.currency()).isEqualTo("EUR");
    }

    @Test
    void shouldApplyHolidayPricingStrategy() {
        Passenger passenger = ticketTestSetup.createPassenger("Tom", "Tester");

        TripExecution tripExecution = ticketTestSetup.createTripExecution(
                "Holiday Line 9",
                DayType.HOLIDAY,
                new BigDecimal("4.00"),
                LocalDateTime.of(2025, 9, 10, 11, 0)
        );

        TicketResponse ticket = ticketService.createTicket(
                new CreateTicketRequest(passenger.getId(), tripExecution.getId())
        );

        assertThat(ticket.price()).isEqualByComparingTo("4.40");
        assertThat(ticket.currency()).isEqualTo("EUR");
    }

    @Test
    void shouldThrowWhenTravelerDoesNotExist() {
        TripExecution tripExecution = ticketTestSetup.createTripExecution(
                "Weekday Line 1",
                DayType.WEEKDAY,
                new BigDecimal("3.20"),
                LocalDateTime.of(2025, 9, 10, 9, 0)
        );

        CreateTicketRequest request = new CreateTicketRequest(999999L, tripExecution.getId());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> ticketService.createTicket(request)
        );

        assertThat(exception.getMessage()).isEqualTo("Person not found with id: 999999");
    }

    @Test
    void shouldThrowWhenTripExecutionDoesNotExist() {
        Passenger passenger = ticketTestSetup.createPassenger("Anna", "Musterfrau");

        CreateTicketRequest request = new CreateTicketRequest(passenger.getId(), 999999L);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> ticketService.createTicket(request)
        );

        assertThat(exception.getMessage()).isEqualTo("TripExecution not found with id: 999999");
    }
}