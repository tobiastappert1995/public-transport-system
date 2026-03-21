package de.tappert.publictransportsystem.integration.setup;

import de.tappert.publictransportsystem.api.internal.dto.DemoDataRequest;
import de.tappert.publictransportsystem.domain.enums.Currency;
import de.tappert.publictransportsystem.domain.enums.DayType;
import de.tappert.publictransportsystem.domain.enums.EngineType;
import de.tappert.publictransportsystem.domain.model.*;
import de.tappert.publictransportsystem.domain.valueobject.Money;
import de.tappert.publictransportsystem.infrastructure.persistence.repository.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Component
public class TripSearchTestSetup {

    private final StopRepository stopRepository;
    private final TravelPathRepository travelPathRepository;
    private final FareRepository fareRepository;
    private final TripRepository tripRepository;
    private final BusRepository busRepository;
    private final BusDriverRepository busDriverRepository;
    private final TripExecutionRepository tripExecutionRepository;
    private final TicketRepository ticketRepository;

    public TripSearchTestSetup(
            StopRepository stopRepository,
            TravelPathRepository travelPathRepository,
            FareRepository fareRepository,
            TripRepository tripRepository,
            BusRepository busRepository,
            BusDriverRepository busDriverRepository,
            TripExecutionRepository tripExecutionRepository,
            TicketRepository ticketRepository
    ) {
        this.stopRepository = stopRepository;
        this.travelPathRepository = travelPathRepository;
        this.fareRepository = fareRepository;
        this.tripRepository = tripRepository;
        this.busRepository = busRepository;
        this.busDriverRepository = busDriverRepository;
        this.tripExecutionRepository = tripExecutionRepository;
        this.ticketRepository = ticketRepository;
    }

    @Transactional
    public void add(DemoDataRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request must not be null");
        }

        List<TravelPath> travelPaths = List.of(
                createTravelPath(
                        request.tripName() + " Path",
                        createStops(request.stopNames(), request.locationPrefix())
                )
        );

        addTrip(
                request.tripName(),
                request.dayType(),
                request.fareAmount(),
                request.currency(),
                request.startExecutionTime(),
                request.numberOfExecutions(),
                request.executionIntervalMinutes(),
                request.busesPerExecution(),
                request.busNumberPrefix(),
                travelPaths
        );
    }

    @Transactional
    public void addTripWithTravelPaths(
            String tripName,
            DayType dayType,
            BigDecimal fareAmount,
            Currency currency,
            LocalDateTime startExecutionTime,
            int numberOfExecutions,
            int executionIntervalMinutes,
            int busesPerExecution,
            String busNumberPrefix,
            List<TravelPathInput> travelPathInputs
    ) {
        if (travelPathInputs == null || travelPathInputs.isEmpty()) {
            throw new IllegalArgumentException("travelPathInputs must not be empty");
        }

        List<TravelPath> travelPaths = travelPathInputs.stream()
                .map(input -> createTravelPath(input.name(), createStops(input.stopNames(), input.locationPrefix())))
                .toList();

        addTrip(
                tripName,
                dayType,
                fareAmount,
                currency,
                startExecutionTime,
                numberOfExecutions,
                executionIntervalMinutes,
                busesPerExecution,
                busNumberPrefix,
                travelPaths
        );
    }

    @Transactional
    public void reset() {
        ticketRepository.deleteAll();
        tripExecutionRepository.deleteAll();
        tripRepository.deleteAll();
        travelPathRepository.deleteAll();
        fareRepository.deleteAll();
        busDriverRepository.deleteAll();
        busRepository.deleteAll();
        stopRepository.deleteAll();
    }

    private void addTrip(
            String tripName,
            DayType dayType,
            BigDecimal fareAmount,
            Currency currency,
            LocalDateTime startExecutionTime,
            int numberOfExecutions,
            int executionIntervalMinutes,
            int busesPerExecution,
            String busNumberPrefix,
            List<TravelPath> travelPaths
    ) {
        Fare fare = fareRepository.save(new Fare(tripName + " Fare", Money.of(fareAmount, currency)));

        Trip trip = new Trip(tripName, dayType, fare);
        travelPaths.forEach(trip::addTravelPath);
        trip.validate();
        Trip savedTrip = tripRepository.save(trip);

        List<TripExecution> executions = new ArrayList<>();
        IntStream.range(0, numberOfExecutions)
                .forEach(index -> executions.add(createExecution(
                        savedTrip,
                        tripName,
                        startExecutionTime,
                        executionIntervalMinutes,
                        busesPerExecution,
                        busNumberPrefix,
                        index
                )));

        tripExecutionRepository.saveAll(executions);
    }

    private List<Stop> createStops(List<String> stopNames, String locationPrefix) {
        return stopNames.stream()
                .map(stopName -> stopRepository.save(new Stop(stopName, locationPrefix, LocalDate.of(2020, 1, 1), null)))
                .toList();
    }

    private TravelPath createTravelPath(String name, List<Stop> stops) {
        TravelPath travelPath = new TravelPath(name);
        IntStream.range(0, stops.size())
                .forEach(index -> travelPath.addStop(stops.get(index), index + 1));
        return travelPathRepository.save(travelPath);
    }

    private TripExecution createExecution(
            Trip trip,
            String tripName,
            LocalDateTime startExecutionTime,
            int executionIntervalMinutes,
            int busesPerExecution,
            String busNumberPrefix,
            int executionIndex
    ) {
        LocalDateTime executionTime = startExecutionTime.plusMinutes((long) executionIndex * executionIntervalMinutes);
        TripExecution execution = new TripExecution(trip, executionTime);
        createBusesForExecution(tripName, busesPerExecution, busNumberPrefix, executionIndex).forEach(execution::addBus);

        List<BusDriver> drivers = createDriversForExecution(executionIndex, busesPerExecution);
        drivers.forEach(execution::addBusDriver);

        execution.validate();
        return execution;
    }

    private List<Bus> createBusesForExecution(
            String tripName,
            int busesPerExecution,
            String busNumberPrefix,
            int executionIndex
    ) {
        List<Bus> buses = new ArrayList<>();

        IntStream.range(0, busesPerExecution)
                .forEach(busIndex -> buses.add(busRepository.save(
                        new Bus(
                                busNumberPrefix + (executionIndex + 1) + "-" + (busIndex + 1),
                                tripName + " Bus " + (executionIndex + 1) + "." + (busIndex + 1),
                                "Van Hool",
                                "AGG 300",
                                80,
                                210,
                                50.0,
                                EngineType.GASOLINE,
                                LocalDate.of(2005, 1, 1)
                        )
                )));

        return buses;
    }

    private List<BusDriver> createDriversForExecution(int executionIndex, int busesPerExecution) {
        List<BusDriver> drivers = new ArrayList<>();

        IntStream.range(0, busesPerExecution)
                .forEach(driverIndex -> drivers.add(busDriverRepository.save(
                        new BusDriver(
                                "Manni" + (executionIndex + 1) + "-" + (driverIndex + 1),
                                "Busfahrer"
                        )
                )));

        return drivers;
    }

    public record TravelPathInput(String name, List<String> stopNames, String locationPrefix) {
    }
}
