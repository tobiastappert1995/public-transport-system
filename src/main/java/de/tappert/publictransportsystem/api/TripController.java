package de.tappert.publictransportsystem.api;

import de.tappert.publictransportsystem.api.dto.TripSearchRequest;
import de.tappert.publictransportsystem.api.dto.TripSearchResponse;
import de.tappert.publictransportsystem.application.service.TripSearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for searching trip executions.
 */
@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripSearchService tripSearchService;

    @PostMapping("/search")
    public List<TripSearchResponse> searchTrips(@Valid @RequestBody TripSearchRequest request) {
        return tripSearchService.search(request);
    }
}
