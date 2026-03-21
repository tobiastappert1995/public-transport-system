package de.tappert.publictransportsystem.integration.setup;

import de.tappert.publictransportsystem.domain.enums.Currency;
import de.tappert.publictransportsystem.domain.enums.DayType;
import de.tappert.publictransportsystem.domain.enums.EngineType;
import de.tappert.publictransportsystem.domain.model.*;
import de.tappert.publictransportsystem.domain.valueobject.Money;
import de.tappert.publictransportsystem.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TicketTestSetup {

    private final TicketRepository ticketRepository;
    private final TripExecutionRepository tripExecutionRepository;
    private final TripRepository tripRepository;
    private final TravelPathRepository travelPathRepository;
    private final FareRepository fareRepository;
    private final BusDriverRepository busDriverRepository;
    private final BusRepository busRepository;
    private final PassengerRepository passengerRepository;
    private final PersonRepository personRepository;
    private final StopRepository stopRepository;

    public void reset() {
        ticketRepository.deleteAll();
        tripExecutionRepository.deleteAll();
        tripRepository.deleteAll();
        travelPathRepository.deleteAll();
        fareRepository.deleteAll();
        busDriverRepository.deleteAll();
        busRepository.deleteAll();
        personRepository.deleteAll();
        stopRepository.deleteAll();
    }

    public Passenger createPassenger(String firstName, String lastName) {
        return passengerRepository.save(new Passenger(firstName, lastName));
    }

    public BusDriver createBusDriver(String firstName, String lastName) {
        return busDriverRepository.save(new BusDriver(firstName, lastName));
    }

    public TripExecution createTripExecution(
            String tripName,
            DayType dayType,
            BigDecimal fareAmount,
            LocalDateTime executionTime
    ) {
        Stop departure = stopRepository.save(new Stop("Stop A", "Aachen", LocalDate.of(2020, 1, 1), null));
        Stop destination = stopRepository.save(new Stop("Stop B", "Aachen", LocalDate.of(2020, 1, 1), null));

        TravelPath travelPath = new TravelPath("Main Path");
        travelPath.addStop(departure, 1);
        travelPath.addStop(destination, 2);
        travelPath = travelPathRepository.save(travelPath);

        Fare fare = fareRepository.save(new Fare(
                tripName + " Fare",
                Money.of(fareAmount, Currency.EUR)
        ));

        Trip trip = new Trip(tripName, dayType, fare);
        trip.addTravelPath(travelPath);
        trip.validate();
        trip = tripRepository.save(trip);

        Bus bus = busRepository.save(new Bus(
                "100-1",
                tripName + " Bus",
                "Van Hool",
                "AGG 300",
                80,
                210,
                50.0,
                EngineType.GASOLINE,
                LocalDate.of(2005, 1, 1)
        ));

        BusDriver driver = busDriverRepository.save(new BusDriver("Driver", "One"));

        TripExecution tripExecution = new TripExecution(trip, executionTime);
        tripExecution.addBus(bus);
        tripExecution.addBusDriver(driver);
        tripExecution.validate();

        return tripExecutionRepository.save(tripExecution);
    }
}