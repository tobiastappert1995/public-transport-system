package de.tappert.publictransportsystem.application.service.internal;


import de.tappert.publictransportsystem.api.internal.dto.DemoDataRequest;
import de.tappert.publictransportsystem.api.internal.dto.DemoDataResponse;
import de.tappert.publictransportsystem.domain.enums.EngineType;
import de.tappert.publictransportsystem.domain.model.*;
import de.tappert.publictransportsystem.domain.valueobject.Money;
import de.tappert.publictransportsystem.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Service that generates and deletes demo data such as stops, trips,
 * travel paths, buses, drivers, and trip executions.
 */
@Service
@RequiredArgsConstructor
public class DemoDataService {

    private final StopRepository stopRepository;
    private final TravelPathRepository travelPathRepository;
    private final FareRepository fareRepository;
    private final TripRepository tripRepository;
    private final BusRepository busRepository;
    private final BusDriverRepository busDriverRepository;
    private final TripExecutionRepository tripExecutionRepository;
    private final TicketRepository ticketRepository;

    @Transactional
    public DemoDataResponse createDemoData(DemoDataRequest request) {
        List<Stop> stops = createStops(request.stopNames(), request.locationPrefix());
        TravelPath travelPath = createTravelPath(request.tripName() + " Path1", stops);
        TravelPath travelPath2 = createTravelPath(request.tripName() + " Path2", stops);
        Fare fare = createFare(request);

        Trip trip = new Trip(request.tripName(), request.dayType(), fare);
        trip.addTravelPath(travelPath);
        trip.addTravelPath(travelPath2);
        trip.validate();

        Trip savedTrip = tripRepository.save(trip);
        createExecutions(savedTrip, request);

        return new DemoDataResponse(
                "Demo data created successfully",
                savedTrip.getId(),
                stops.size(),
                request.numberOfExecutions()
        );
    }

    @Transactional
    public void deleteAllDemoData() {
        ticketRepository.deleteAll();
        tripExecutionRepository.deleteAll();
        tripRepository.deleteAll();
        travelPathRepository.deleteAll();
        fareRepository.deleteAll();
        busDriverRepository.deleteAll();
        busRepository.deleteAll();
        stopRepository.deleteAll();
    }

    private List<Stop> createStops(List<String> stopNames, String locationPrefix) {
        List<Stop> stops = new ArrayList<>();

        for (String stopName : stopNames) {
            stops.add(new Stop(
                    stopName,
                    locationPrefix,
                    LocalDate.of(2020, 1, 1),
                    null
            ));
        }

        return stopRepository.saveAll(stops);
    }

    private TravelPath createTravelPath(String name, List<Stop> stops) {
        TravelPath travelPath = new TravelPath(name);

        IntStream.range(0, stops.size())
                .forEach(index -> travelPath.addStop(stops.get(index), index + 1));

        return travelPathRepository.save(travelPath);
    }

    private Fare createFare(DemoDataRequest request) {
        return fareRepository.save(new Fare(
                request.tripName() + " Fare",
                Money.of(request.fareAmount(), request.currency())
        ));
    }

    private void createExecutions(Trip trip, DemoDataRequest request) {
        List<TripExecution> executions = new ArrayList<>();

        IntStream.range(0, request.numberOfExecutions())
                .forEach(executionIndex -> executions.add(createTripExecution(trip, request, executionIndex)));

        tripExecutionRepository.saveAll(executions);
    }

    private TripExecution createTripExecution(Trip trip, DemoDataRequest request, int executionIndex) {
        LocalDateTime executionTime = request.startExecutionTime()
                .plusMinutes((long) executionIndex * request.executionIntervalMinutes());

        TripExecution tripExecution = new TripExecution(trip, executionTime);

        List<Bus> buses = createBusesForExecution(request, executionIndex);
        buses.forEach(tripExecution::addBus);

        BusDriver driver = busDriverRepository.save(
                new BusDriver("Driver" + (executionIndex + 1), "One")
        );
        tripExecution.addBusDriver(driver);

        tripExecution.validate();
        return tripExecution;
    }

    private List<Bus> createBusesForExecution(DemoDataRequest request, int executionIndex) {
        List<Bus> buses = new ArrayList<>();

        IntStream.range(0, request.busesPerExecution())
                .forEach(busIndex -> buses.add(new Bus(
                        request.busNumberPrefix() + (executionIndex + 1) + "-" + (busIndex + 1),
                        request.tripName() + " Bus " + (executionIndex + 1) + "." + (busIndex + 1),
                        "Van Hool",
                        "AGG 300",
                        80,
                        210,
                        50.0,
                        EngineType.GASOLINE,
                        LocalDate.of(2005, 1, 1)
                )));

        return busRepository.saveAll(buses);
    }
}