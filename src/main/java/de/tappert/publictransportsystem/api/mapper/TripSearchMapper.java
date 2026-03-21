package de.tappert.publictransportsystem.api.mapper;

import de.tappert.publictransportsystem.api.dto.TravelPathResponse;
import de.tappert.publictransportsystem.api.dto.TripSearchResponse;
import de.tappert.publictransportsystem.api.dto.TripStopResponse;
import de.tappert.publictransportsystem.domain.model.Stop;
import de.tappert.publictransportsystem.domain.model.TravelPath;
import de.tappert.publictransportsystem.domain.model.TravelPathStop;
import de.tappert.publictransportsystem.domain.model.TripExecution;
import de.tappert.publictransportsystem.domain.valueobject.Money;

import java.util.List;

/**
 * Utility class for mapping {@link TripExecution} entities to {@link TripSearchResponse} DTOs.
 */
public final class TripSearchMapper {

    private TripSearchMapper() {
    }

    public static TripSearchResponse toResponse(TripExecution tripExecution, Money ticketPrice) {
        return new TripSearchResponse(
                tripExecution.getId(),
                tripExecution.getExecutionTime(),
                tripExecution.getTrip().getName(),
                ticketPrice.getAmount(),
                ticketPrice.getCurrency().name(),
                mapTravelPaths(tripExecution)
        );
    }

    private static List<TravelPathResponse> mapTravelPaths(TripExecution tripExecution) {
        return tripExecution.getTrip().getTravelPaths().stream()
                .map(TripSearchMapper::mapTravelPath)
                .toList();
    }

    private static TravelPathResponse mapTravelPath(TravelPath travelPath) {
        return new TravelPathResponse(
                travelPath.getName(),
                mapDepartureStop(travelPath),
                mapDestinationStop(travelPath),
                mapStops(travelPath)
        );
    }

    private static String mapDepartureStop(TravelPath travelPath) {
        return travelPath.getFirstStop()
                .map(Stop::getName)
                .orElse(null);
    }

    private static String mapDestinationStop(TravelPath travelPath) {
        return travelPath.getLastStop()
                .map(Stop::getName)
                .orElse(null);
    }

    private static List<TripStopResponse> mapStops(TravelPath travelPath) {
        return travelPath.getStopsInOrder().stream()
                .map(TripSearchMapper::mapStop)
                .toList();
    }

    private static TripStopResponse mapStop(TravelPathStop travelPathStop) {
        return new TripStopResponse(
                travelPathStop.getSequenceNumber(),
                travelPathStop.getStop().getName(),
                travelPathStop.getStop().getLocation()
        );
    }
}