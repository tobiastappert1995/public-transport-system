package de.tappert.publictransportsystem.api.internal;

import de.tappert.publictransportsystem.api.internal.dto.DemoDataRequest;
import de.tappert.publictransportsystem.api.internal.dto.DemoDataResponse;
import de.tappert.publictransportsystem.application.service.internal.DemoDataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Internal REST controller for creating and deleting demo data.
 */
@RestController
@RequestMapping("api/internal/demo-data")
@RequiredArgsConstructor
public class DemoDataController {

    private final DemoDataService demoDataService;

    @PostMapping
    public DemoDataResponse createDemoData(@Valid @RequestBody DemoDataRequest request) {
        return demoDataService.createDemoData(request);
    }

    @DeleteMapping
    public void deleteAllDemoData() {
        demoDataService.deleteAllDemoData();
    }
}