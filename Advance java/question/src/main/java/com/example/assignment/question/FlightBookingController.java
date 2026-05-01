package com.example.assignment.question;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Flight Booking Controller - Q1 REST API
 * Handles flight booking operations using JDBC with transaction management
 * 
 * Endpoint: POST /book
 * Parameters: flightId, passengerName, seatsRequested
 */
@RestController
@RequestMapping("/api")
public class FlightBookingController {

    @Autowired
    private FlightBookingService flightBookingService;

    /**
     * Book a flight (Q1 Requirement)
     * Accepts flight_id, passenger_name, and seats_requested
     * Performs transaction with commit/rollback logic
     * 
     * @param flightId the flight ID
     * @param passengerName the passenger name
     * @param seatsRequested number of seats to book
     * @return booking status message
     * 
     * Example:
     * POST /api/book?flightId=1&passengerName=John%20Doe&seatsRequested=2
     */
    @PostMapping("/book")
    public ResponseEntity<String> bookFlight(
            @RequestParam(name = "flightId") int flightId,
            @RequestParam(name = "passengerName") String passengerName,
            @RequestParam(name = "seatsRequested") int seatsRequested) {
        
        try {
            // Validate inputs
            if (flightId <= 0) {
                return ResponseEntity.badRequest().body("Invalid flight ID");
            }
            if (passengerName == null || passengerName.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Passenger name cannot be empty");
            }
            if (seatsRequested <= 0) {
                return ResponseEntity.badRequest().body("Seats requested must be greater than 0");
            }

            // Call booking service with JDBC transaction management
            String result = flightBookingService.bookFlight(flightId, passengerName, seatsRequested);
            
            // Return success or failure based on result
            if (result.contains("Successful")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Booking Failed: " + e.getMessage());
        }
    }

    /**
     * Health check endpoint
     * 
     * @return health status
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Flight Booking Service is running!");
    }
}
