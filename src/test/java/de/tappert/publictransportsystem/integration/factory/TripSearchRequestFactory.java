package de.tappert.publictransportsystem.integration.factory;

import de.tappert.publictransportsystem.api.dto.TripSearchRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class TripSearchRequestFactory {

    private TripSearchRequestFactory() {
    }

    public static TripSearchRequest empty() {
        return new TripSearchRequest(null, null, null, null);
    }

    public static TripSearchRequest byLocation(String location) {
        return new TripSearchRequest(null, null, location, null);
    }

    public static TripSearchRequest byMaxPrice(String maxPrice) {
        return new TripSearchRequest(null, null, null, new BigDecimal(maxPrice));
    }

    public static TripSearchRequest byUntil(LocalDateTime until) {
        return new TripSearchRequest(null, until, null, null);
    }

    public static TripSearchRequest byRange(LocalDateTime from, LocalDateTime until) {
        return new TripSearchRequest(from, until, null, null);
    }
}

