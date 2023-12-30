package com.SCU.pose.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SystemController {

    /**
     * Endpoint to signal that the backend is ready and operational.
     *
     * @return A success message with a status code.
     */
    @GetMapping("/start")
    public ResponseEntity<String> startSystem() {
        // Perform any initialization if necessary, e.g., warming up cache, preloading data, etc.

        // Return a response entity with a 200 OK status code and a message
        return ResponseEntity.ok("Backend is ready and operational.");
    }


}
