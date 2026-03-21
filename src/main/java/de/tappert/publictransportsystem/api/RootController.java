package de.tappert.publictransportsystem.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

    @GetMapping("/")
    public String status() {
        return "Public Transport System is running";
    }
}