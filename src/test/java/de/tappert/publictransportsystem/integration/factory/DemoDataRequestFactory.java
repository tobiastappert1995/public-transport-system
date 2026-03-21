package de.tappert.publictransportsystem.integration.factory;


import de.tappert.publictransportsystem.api.internal.dto.DemoDataRequest;
import de.tappert.publictransportsystem.domain.enums.Currency;
import de.tappert.publictransportsystem.domain.enums.DayType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public final class DemoDataRequestFactory {

    private DemoDataRequestFactory() {
    }

    public static DemoDataRequest weekdayLine1() {
        return new DemoDataRequest(
                "Weekday Line 1",
                DayType.WEEKDAY,
                new BigDecimal("3.20"),
                Currency.EUR,
                LocalDateTime.of(2025, 9, 10, 9, 0),
                2,
                60,
                1,
                "194-",
                List.of("Bushof", "Elisenbrunnen", "Hauptbahnhof", "Rothe Erde"),
                "Aachen"
        );
    }

    public static DemoDataRequest weekendLine7() {
        return new DemoDataRequest(
                "Weekend Line 7",
                DayType.WEEKEND,
                new BigDecimal("4.00"),
                Currency.EUR,
                LocalDateTime.of(2025, 9, 10, 9, 30),
                1,
                60,
                2,
                "300-",
                List.of("Markt", "Ringstraße", "Kirche"),
                "Brand"
        );
    }

    public static DemoDataRequest holidayLine9() {
        return new DemoDataRequest(
                "Holiday Line 9",
                DayType.HOLIDAY,
                new BigDecimal("5.50"),
                Currency.EUR,
                LocalDateTime.of(2025, 9, 10, 11, 0),
                1,
                60,
                1,
                "500-",
                List.of("Dom", "Neumarkt", "Deutz"),
                "Cologne"
        );
    }
}

