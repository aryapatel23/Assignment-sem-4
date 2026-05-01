package com.example.assignment.question;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Flight Booking Service - JDBC Implementation for Q1
 * Handles flight booking with transaction management
 * Uses PostgreSQL database (airlinedb)
 * 
 * Database Setup Required:
 * CREATE DATABASE airlinedb;
 * 
 * CREATE TABLE flights (
 *     flight_id SERIAL PRIMARY KEY,
 *     flight_name VARCHAR(100) NOT NULL,
 *     available_seats INT NOT NULL,
 *     price_per_seat DECIMAL(10, 2) NOT NULL
 * );
 * 
 * CREATE TABLE bookings (
 *     booking_id SERIAL PRIMARY KEY,
 *     passenger_name VARCHAR(100) NOT NULL,
 *     flight_id INT NOT NULL REFERENCES flights(flight_id),
 *     seats_booked INT NOT NULL,
 *     total_amount DECIMAL(10, 2) NOT NULL,
 *     booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
 * );
 * 
 * Sample Insert:
 * INSERT INTO flights VALUES (1, 'Flight AI-101', 100, 5000.00);
 * INSERT INTO flights VALUES (2, 'Flight BA-202', 50, 8000.00);
 */
@Service
public class FlightBookingService {

    private final DataSource airlineDataSource;

    public FlightBookingService(@Qualifier("airlineDataSource") DataSource airlineDataSource) {
        this.airlineDataSource = airlineDataSource;
    }

    /**
     * Book a flight with transaction management
     * 
     * @param flightId the flight to book
     * @param passengerName the passenger name
     * @param seatsRequested number of seats to book
     * @return booking status message
     */
    public String bookFlight(int flightId, String passengerName, int seatsRequested) {
        Connection conn = null;
        try {
            // Get connection from connection pool (HikariCP)
            conn = airlineDataSource.getConnection();
            
            // Set AutoCommit to false for transaction management
            conn.setAutoCommit(false);

            // 1. Check available seats in flights table
            String checkSeatsQuery = "SELECT flight_name, available_seats, price_per_seat FROM flights WHERE flight_id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSeatsQuery)) {
                checkStmt.setInt(1, flightId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        int availableSeats = rs.getInt("available_seats");
                        double pricePerSeat = rs.getDouble("price_per_seat");
                        String flightName = rs.getString("flight_name");

                        if (availableSeats >= seatsRequested) {
                            // 2. Deduct seats_requested from available_seats
                            String updateFlightsQuery = "UPDATE flights SET available_seats = available_seats - ? WHERE flight_id = ?";
                            try (PreparedStatement updateStmt = conn.prepareStatement(updateFlightsQuery)) {
                                updateStmt.setInt(1, seatsRequested);
                                updateStmt.setInt(2, flightId);
                                updateStmt.executeUpdate();
                            }

                            // 3. Insert a record into bookings with total_amount
                            double totalAmount = seatsRequested * pricePerSeat;
                            String insertBookingQuery = "INSERT INTO bookings (passenger_name, flight_id, seats_booked, total_amount) VALUES (?, ?, ?, ?)";
                            try (PreparedStatement insertStmt = conn.prepareStatement(insertBookingQuery)) {
                                insertStmt.setString(1, passengerName);
                                insertStmt.setInt(2, flightId);
                                insertStmt.setInt(3, seatsRequested);
                                insertStmt.setDouble(4, totalAmount);
                                insertStmt.executeUpdate();
                            }

                            // 4. Commit transaction
                            conn.commit();
                            String successMsg = String.format("Booking Successful! Flight: %s, Passenger: %s, Seats: %d, Total: Rs. %.2f",
                                    flightName, passengerName, seatsRequested, totalAmount);
                            System.out.println(successMsg);
                            return successMsg;
                        } else {
                            // Insufficient seats -> rollback
                            conn.rollback();
                            String failMsg = "Booking Failed: Not enough seats available";
                            System.out.println(failMsg);
                            return failMsg;
                        }
                    } else {
                        conn.rollback();
                        String notFoundMsg = "Booking Failed: Flight not found";
                        System.out.println(notFoundMsg);
                        return notFoundMsg;
                    }
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            String errorMsg = "Booking Failed: Database error occurred - " + e.getMessage();
            System.out.println(errorMsg);
            return errorMsg;
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Fetch all flights for the live dashboard preview.
     *
     * @return list of flight records
     */
    public List<Map<String, Object>> getAllFlights() {
        List<Map<String, Object>> flights = new ArrayList<>();
        String query = "SELECT flight_id, flight_name, available_seats, price_per_seat FROM flights ORDER BY flight_id";

        try (Connection conn = airlineDataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Map<String, Object> flight = new LinkedHashMap<>();
                flight.put("flightId", resultSet.getInt("flight_id"));
                flight.put("flightName", resultSet.getString("flight_name"));
                flight.put("availableSeats", resultSet.getInt("available_seats"));
                flight.put("pricePerSeat", resultSet.getDouble("price_per_seat"));
                flights.add(flight);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to load flights: " + e.getMessage(), e);
        }

        return flights;
    }

    /**
     * Fetch all bookings for the live dashboard preview.
     *
     * @return list of booking records
     */
    public List<Map<String, Object>> getAllBookings() {
        List<Map<String, Object>> bookings = new ArrayList<>();
        String query = "SELECT booking_id, passenger_name, flight_id, seats_booked, total_amount, booking_date FROM bookings ORDER BY booking_id DESC";

        try (Connection conn = airlineDataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Map<String, Object> booking = new LinkedHashMap<>();
                booking.put("bookingId", resultSet.getInt("booking_id"));
                booking.put("passengerName", resultSet.getString("passenger_name"));
                booking.put("flightId", resultSet.getInt("flight_id"));
                booking.put("seatsBooked", resultSet.getInt("seats_booked"));
                booking.put("totalAmount", resultSet.getDouble("total_amount"));
                booking.put("bookingDate", resultSet.getTimestamp("booking_date").toString());
                bookings.add(booking);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to load bookings: " + e.getMessage(), e);
        }

        return bookings;
    }
}
