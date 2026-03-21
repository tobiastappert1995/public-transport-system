package de.tappert.publictransportsystem.integration;

import de.tappert.publictransportsystem.api.dto.TravelPathResponse;
import de.tappert.publictransportsystem.api.dto.TripSearchRequest;
import de.tappert.publictransportsystem.api.dto.TripSearchResponse;
import de.tappert.publictransportsystem.api.dto.TripStopResponse;
import de.tappert.publictransportsystem.application.service.TripSearchService;
import de.tappert.publictransportsystem.domain.enums.Currency;
import de.tappert.publictransportsystem.domain.enums.DayType;
import de.tappert.publictransportsystem.integration.factory.DemoDataRequestFactory;
import de.tappert.publictransportsystem.integration.factory.TripSearchRequestFactory;
import de.tappert.publictransportsystem.integration.setup.TripSearchTestSetup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@SpringBootTest
@ActiveProfiles("test")
class TripSearchServiceIntegrationTest {

    private static final LocalDateTime EXECUTION_0900 = LocalDateTime.of(2025, 9, 10, 9, 0);
    private static final LocalDateTime EXECUTION_0930 = LocalDateTime.of(2025, 9, 10, 9, 30);
    private static final LocalDateTime EXECUTION_1000 = LocalDateTime.of(2025, 9, 10, 10, 0);
    private static final LocalDateTime EXECUTION_1100 = LocalDateTime.of(2025, 9, 10, 11, 0);
    private static final LocalDateTime EXECUTION_1200 = LocalDateTime.of(2025, 9, 10, 12, 0);

    private static final String WEEKDAY_LINE_1 = "Weekday Line 1";
    private static final String WEEKEND_LINE_7 = "Weekend Line 7";
    private static final String MULTI_PATH_LINE = "Weekday Line 42";

    @Autowired
    private TripSearchService tripSearchService;

    @Autowired
    private TripSearchTestSetup tripSearchTestSetup;

    private static Stream<Arguments> searchCases() {
        return Stream.of(
                arguments("all filters null -> all 4 executions", TripSearchRequestFactory.empty(), 4),
                arguments("location Aachen -> 2 executions", TripSearchRequestFactory.byLocation("Aachen"), 2),
                arguments("location Brand -> 1 execution", TripSearchRequestFactory.byLocation("Brand"), 1),
                arguments("max price 3.50 -> 2 executions", TripSearchRequestFactory.byMaxPrice("3.50"), 2),
                arguments(
                        "until 10:00 -> 3 executions",
                        TripSearchRequestFactory.byUntil(LocalDateTime.of(2025, 9, 10, 10, 0)),
                        3
                ),
                arguments(
                        "range 09:15 to 10:30 -> 2 executions",
                        TripSearchRequestFactory.byRange(
                                LocalDateTime.of(2025, 9, 10, 9, 15),
                                LocalDateTime.of(2025, 9, 10, 10, 30)
                        ),
                        2
                )
        );
    }

    @BeforeEach
    void setUp() {
        tripSearchTestSetup.reset();
        tripSearchTestSetup.add(DemoDataRequestFactory.weekdayLine1());
        tripSearchTestSetup.add(DemoDataRequestFactory.weekendLine7());
        tripSearchTestSetup.add(DemoDataRequestFactory.holidayLine9());
    }

    @AfterEach
    void tearDown() {
        tripSearchTestSetup.reset();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("searchCases")
    @DisplayName("should return expected number of trip executions for different scenarios")
    void shouldReturnExpectedNumberOfTripExecutions(
            String testName,
            TripSearchRequest request,
            int expectedSize
    ) {
        assertThat(search(request)).hasSize(expectedSize);
    }

    @Test
    void shouldReturnTravelPathStopsInResponse() {
        TripSearchResponse execution = findByTripAndTime(
                searchByLocation("Aachen"),
                WEEKDAY_LINE_1,
                EXECUTION_0900
        );

        assertThat(execution.travelPaths()).hasSize(1);

        TravelPathResponse travelPath = execution.travelPaths().getFirst();
        assertThat(travelPath.departureStop()).isEqualTo("Bushof");
        assertThat(travelPath.destinationStop()).isEqualTo("Rothe Erde");
        assertThat(travelPath.stops().stream().map(TripStopResponse::stopName).toList())
                .contains("Bushof", "Elisenbrunnen", "Hauptbahnhof", "Rothe Erde");
        assertThat(travelPath.stops().stream().map(TripStopResponse::order).toList())
                .contains(1, 2, 3, 4);
    }

    @Test
    void shouldReturnResultsSortedByExecutionTimeAscending() {
        assertThat(search(TripSearchRequestFactory.empty()).stream()
                .map(TripSearchResponse::executionTime)
                .toList())
                .contains(EXECUTION_0900, EXECUTION_0930, EXECUTION_1000, EXECUTION_1100);
    }

    @Test
    void shouldReturnCalculatedTicketPriceInSearchResponse() {
        TripSearchResponse execution = findByTripAndTime(
                searchByLocation("Brand"),
                WEEKEND_LINE_7,
                EXECUTION_0930
        );

        assertThat(execution.ticketPrice()).isEqualByComparingTo("4.20");
        assertThat(execution.currency()).isEqualTo("EUR");
    }

    @Test
    void shouldReturnAllTravelPathsForTrip() {
        addMultiPathTrip();

        TripSearchResponse execution = findByTripAndTime(
                search(TripSearchRequestFactory.empty()),
                MULTI_PATH_LINE,
                EXECUTION_1200
        );

        assertThat(execution.travelPaths()).hasSize(2);
        assertThat(execution.travelPaths().stream().map(TravelPathResponse::name).toList())
                .containsExactly("Weekday Line 42 Outbound", "Weekday Line 42 Return");

        TravelPathResponse outbound = findTravelPath(execution, "Weekday Line 42 Outbound");
        assertThat(outbound.departureStop()).isEqualTo("Bushof");
        assertThat(outbound.destinationStop()).isEqualTo("Brand End");
        assertThat(outbound.stops().stream().map(TripStopResponse::stopName).toList())
                .containsExactly("Bushof", "Normaluhr", "Brand End");

        TravelPathResponse inbound = findTravelPath(execution, "Weekday Line 42 Return");
        assertThat(inbound.departureStop()).isEqualTo("Brand End");
        assertThat(inbound.destinationStop()).isEqualTo("Bushof");
        assertThat(inbound.stops().stream().map(TripStopResponse::stopName).toList())
                .containsExactly("Brand End", "Hauptbahnhof", "Bushof");
    }

    @Test
    void shouldFindTripWhenLocationMatchesAnyTravelPath() {
        addMultiPathTrip();

        List<TripSearchResponse> result = searchByLocation("Aachen East");

        TripSearchResponse response = findByTripAndTime(result, MULTI_PATH_LINE, EXECUTION_1200);
        assertThat(response.tripName()).isEqualTo(MULTI_PATH_LINE);
        assertThat(response.executionTime()).isEqualTo(EXECUTION_1200);
    }

    @Test
    void shouldThrowWhenFromIsAfterUntil() {
        TripSearchRequest request = new TripSearchRequest(
                LocalDateTime.of(2025, 9, 10, 11, 0),
                LocalDateTime.of(2025, 9, 10, 9, 0),
                null,
                null
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tripSearchService.search(request)
        );

        assertThat(exception.getMessage()).isEqualTo("from must not be after until");
    }

    private void addMultiPathTrip() {
        tripSearchTestSetup.addTripWithTravelPaths(
                MULTI_PATH_LINE,
                DayType.WEEKDAY,
                new BigDecimal("3.80"),
                Currency.EUR,
                EXECUTION_1200,
                1,
                60,
                1,
                "420-",
                List.of(
                        new TripSearchTestSetup.TravelPathInput(
                                "Weekday Line 42 Outbound",
                                List.of("Bushof", "Normaluhr", "Brand End"),
                                "Aachen Center"
                        ),
                        new TripSearchTestSetup.TravelPathInput(
                                "Weekday Line 42 Return",
                                List.of("Brand End", "Hauptbahnhof", "Bushof"),
                                "Aachen East"
                        )
                )
        );
    }

    private List<TripSearchResponse> search(TripSearchRequest request) {
        return tripSearchService.search(request);
    }

    private List<TripSearchResponse> searchByLocation(String location) {
        return search(TripSearchRequestFactory.byLocation(location));
    }

    private TravelPathResponse findTravelPath(TripSearchResponse response, String travelPathName) {
        return response.travelPaths().stream()
                .filter(path -> path.name().equals(travelPathName))
                .findFirst()
                .orElseThrow();
    }

    private TripSearchResponse findByTripAndTime(
            List<TripSearchResponse> result,
            String tripName,
            LocalDateTime executionTime
    ) {
        return result.stream()
                .filter(response -> response.tripName().equals(tripName))
                .filter(response -> response.executionTime().equals(executionTime))
                .findFirst()
                .orElseThrow();
    }
}