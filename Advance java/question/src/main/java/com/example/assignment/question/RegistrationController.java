package com.example.assignment.question;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/**
 * Registration Controller - Q2 REST API
 * Handles user registration endpoint
 * 
 * Endpoint: POST /register
 * Parameters: name (RequestParam), email (RequestParam)
 * Response: "User Registered: <name> | Email: <email>"
 */
@RestController
@RequestMapping("/api")
public class RegistrationController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private FlightBookingService flightBookingService;

    /**
     * Q2 Requirement: Simple POST /register endpoint
     * Accepts name and email as RequestParam and returns formatted string
     * 
     * @param name the user's name
     * @param email the user's email
     * @return success message with user details
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestParam String name, @RequestParam String email) {
        try {
            // Validate inputs
            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Name cannot be empty");
            }
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Email cannot be empty");
            }

            // Create basic response as per Q2 requirement
            String response = "User Registered: " + name + " | Email: " + email;
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Registration failed: " + e.getMessage());
        }
    }

    /**
     * Enhanced Registration Endpoint (Q3)
     * Registers a student with all required details
     * 
     * @param student the student entity to register
     * @return the registered student with details
     */
    @PostMapping("/student/register")
    public ResponseEntity<?> registerStudent(@Valid @RequestBody Student student) {
        try {
            Student registeredStudent = studentService.registerStudent(student);
            String response = String.format(
                    "Student Registered Successfully! ID: %d, Name: %s, Email: %s, Course: %s",
                    registeredStudent.getId(),
                    registeredStudent.getName(),
                    registeredStudent.getEmail(),
                    registeredStudent.getCourse()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error during registration: " + e.getMessage());
        }
    }

    /**
     * Get student by ID
     * 
     * @param id the student ID
     * @return the student details
     */
    @GetMapping("/student/{id}")
    public ResponseEntity<?> getStudent(@PathVariable Long id) {
        try {
            Student student = studentService.getStudentById(id);
            return ResponseEntity.ok(student);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Update student information
     * 
     * @param id the student ID
     * @param updatedStudent the updated student data
     * @return the updated student
     */
    @PutMapping("/student/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable Long id, @RequestBody Student updatedStudent) {
        try {
            Student student = studentService.updateStudent(id, updatedStudent);
            return ResponseEntity.ok("Student updated successfully: " + student);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Delete student
     * 
     * @param id the student ID
     * @return success message
     */
    @DeleteMapping("/student/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable Long id) {
        try {
            studentService.deleteStudent(id);
            return ResponseEntity.ok("Student deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Live dashboard data for the bottom preview section.
     *
     * @return flights, bookings, and students in one response
     */
    @GetMapping("/dashboard/data")
    public ResponseEntity<?> getDashboardData() {
        try {
            Map<String, Object> dashboardData = new LinkedHashMap<>();
            dashboardData.put("flights", flightBookingService.getAllFlights());
            dashboardData.put("bookings", flightBookingService.getAllBookings());
            dashboardData.put("students", studentService.getAllStudents());
            return ResponseEntity.ok(dashboardData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to load dashboard data: " + e.getMessage());
        }
    }
}
