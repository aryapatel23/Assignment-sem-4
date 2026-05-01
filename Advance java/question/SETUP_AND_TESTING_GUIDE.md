# Advanced Java Assignment - Complete Setup Guide

This project implements all three questions with PostgreSQL database and Spring Boot REST APIs.

---

## Prerequisites

1. **Java 17 or Higher** - Ensure Java is installed
   ```bash
   java -version
   ```

2. **PostgreSQL Server** - Download and install from https://www.postgresql.org/
   - Ensure PostgreSQL is running
   - Default port: 5432

3. **Maven** - For building and running the project
   ```bash
   mvn -version
   ```

4. **Postman or cURL** - For testing REST endpoints (optional)

---

## Database Setup for Q1 (Flight Booking)

Connect to PostgreSQL and execute:

```sql
-- Create airline database
CREATE DATABASE airlinedb;

-- Connect to airlinedb
\c airlinedb;

-- Create flights table
CREATE TABLE flights (
    flight_id SERIAL PRIMARY KEY,
    flight_name VARCHAR(100) NOT NULL,
    available_seats INT NOT NULL CHECK (available_seats >= 0),
    price_per_seat DECIMAL(10, 2) NOT NULL CHECK (price_per_seat > 0)
);

-- Create bookings table
CREATE TABLE bookings (
    booking_id SERIAL PRIMARY KEY,
    passenger_name VARCHAR(100) NOT NULL,
    flight_id INT NOT NULL REFERENCES flights(flight_id),
    seats_booked INT NOT NULL CHECK (seats_booked > 0),
    total_amount DECIMAL(10, 2) NOT NULL,
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert sample flight data
INSERT INTO flights (flight_name, available_seats, price_per_seat) 
VALUES 
    ('AI-101 Mumbai to Delhi', 100, 5000.00),
    ('BA-202 London to Paris', 80, 8000.00),
    ('SG-303 Singapore Express', 120, 6500.00);

-- Verify the data
SELECT * FROM flights;
```

---

## Database Setup for Q3 (Student Management)

```sql
-- Create student database
CREATE DATABASE studentdb;

-- Connect to studentdb
\c studentdb;

-- The students table will be auto-created by Hibernate with ddl-auto=update
-- But you can pre-create it if needed:
CREATE TABLE IF NOT EXISTS students (
    id BIGSERIAL PRIMARY KEY,
    course VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(10)
);

-- Verify table creation
\d students;
```

---

## Application Configuration

The application is configured in `src/main/resources/application.properties`:

```properties
# PostgreSQL Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/studentdb
spring.datasource.username=postgres
spring.datasource.password=postgres  # Change if your password is different

# Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Server Port
server.port=8080
```

**Important:** Update the username and password if your PostgreSQL credentials are different.

---

## Building and Running the Application

### 1. Build the Project
```bash
mvn clean install
```

### 2. Run the Application
```bash
mvn spring-boot:run
```

Or from IDE:
- Right-click the project → Run As → Spring Boot App

The application will start on `http://localhost:8080`

---

## Testing the Endpoints

### Q2: Simple Registration Endpoint (Basic)

**Test with Browser or Postman:**

```
POST http://localhost:8080/api/register?name=John%20Doe&email=john@example.com
```

**Expected Response:**
```
User Registered: John Doe | Email: john@example.com
```

**Using cURL:**
```bash
curl -X POST "http://localhost:8080/api/register?name=John%20Doe&email=john@example.com"
```

---

### Q1: Flight Booking Endpoint (JDBC with Transactions)

**Endpoint:**
```
POST http://localhost:8080/api/book
```

**Parameters:**
- `flightId` (int): The flight ID from flights table (1, 2, or 3)
- `passengerName` (String): Name of the passenger
- `seatsRequested` (int): Number of seats to book

**Test Cases:**

#### Case 1: Successful Booking (10 seats from flight 1)
```bash
curl -X POST "http://localhost:8080/api/book?flightId=1&passengerName=Alice%20Smith&seatsRequested=10"
```

**Expected Response:**
```
Booking Successful! Flight: AI-101 Mumbai to Delhi, Passenger: Alice Smith, Seats: 10, Total: Rs. 50000.00
```

#### Case 2: Insufficient Seats (more seats than available)
```bash
curl -X POST "http://localhost:8080/api/book?flightId=1&passengerName=Bob%20Johnson&seatsRequested=200"
```

**Expected Response:**
```
Booking Failed: Not enough seats available
```

#### Case 3: Invalid Flight
```bash
curl -X POST "http://localhost:8080/api/book?flightId=999&passengerName=Charlie%20Brown&seatsRequested=5"
```

**Expected Response:**
```
Booking Failed: Flight not found
```

**Postman Test:**
1. Create a new POST request
2. URL: `http://localhost:8080/api/book`
3. Go to Params tab
4. Add params: flightId, passengerName, seatsRequested
5. Click Send

---

### Q3: Student Registration Endpoint (JPA with Validation)

**Endpoint:**
```
POST http://localhost:8080/api/student/register
```

**Request Body (JSON):**
```json
{
    "name": "Raj Kumar",
    "email": "raj@example.com",
    "course": "B.Tech Computer Science",
    "phone": "9876543210"
}
```

**Using cURL:**
```bash
curl -X POST http://localhost:8080/api/student/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Raj Kumar",
    "email": "raj@example.com",
    "course": "B.Tech Computer Science",
    "phone": "9876543210"
  }'
```

**Expected Response:**
```
Student Registered Successfully! ID: 1, Name: Raj Kumar, Email: raj@example.com, Course: B.Tech Computer Science
```

---

### Q3: Additional Student Management Endpoints

#### Get Student by ID
```
GET http://localhost:8080/api/student/1
```

**Response:**
```json
{
    "id": 1,
    "name": "Raj Kumar",
    "email": "raj@example.com",
    "course": "B.Tech Computer Science",
    "phone": "9876543210"
}
```

#### Update Student
```
PUT http://localhost:8080/api/student/1
```

**Request Body:**
```json
{
    "name": "Raj Kumar Updated",
    "course": "M.Tech Computer Science"
}
```

#### Delete Student
```
DELETE http://localhost:8080/api/student/1
```

---

## Verifying Database Changes

### For Flight Bookings (Q1)

```sql
-- Connect to airlinedb
\c airlinedb;

-- Check flights table after bookings
SELECT * FROM flights;

-- Check bookings table
SELECT * FROM bookings;

-- Check booking history for a specific passenger
SELECT * FROM bookings WHERE passenger_name = 'Alice Smith';
```

### For Student Records (Q3)

```sql
-- Connect to studentdb
\c studentdb;

-- Check students table
SELECT * FROM students;

-- Check student by email
SELECT * FROM students WHERE email = 'raj@example.com';

-- Check students by course
SELECT * FROM students WHERE course LIKE '%Computer Science%';
```

---

## Architecture & Best Practices Implemented

### Q1 - Flight Booking (JDBC)
✅ **Pure JDBC with Transaction Management**
- `setAutoCommit(false)` for manual transaction control
- `commit()` on successful booking
- `rollback()` on insufficient seats or errors
- HikariCP connection pooling for performance
- Try-with-resources for resource management
- Parameterized queries to prevent SQL injection

### Q2 - Registration (REST Controller)
✅ **REST Endpoint Design**
- Clear endpoint structure: `/api/register`
- Request parameters validation
- Error handling with appropriate HTTP status codes
- Consistent response format

### Q3 - Student Management (JPA/Hibernate)
✅ **ORM Best Practices**
- JPA annotations for entity mapping
- Validation annotations (NotBlank, Email, Pattern)
- Service layer for business logic
- Repository pattern with Spring Data JPA
- Transactional operations
- PostgreSQL with ddl-auto=update for schema management

### General Best Practices
✅ Dependency Injection with Spring
✅ Exception handling and logging
✅ Proper documentation and comments
✅ PostgreSQL for production-grade database
✅ Maven for dependency management
✅ Separation of concerns (Controller → Service → Repository/JDBC)

---

## Troubleshooting

### PostgreSQL Connection Issues
```
Error: Connection refused
Solution: Ensure PostgreSQL is running
- Windows: Services → PostgreSQL → Start
- Linux: sudo systemctl start postgresql
- Mac: brew services start postgresql
```

### Database Not Found
```
Error: database "studentdb" does not exist
Solution: Run the database setup SQL scripts above
```

### Duplicate Email in Student Registration
```
Error: Duplicate email
Solution: Use a unique email for each student
```

### Port Already in Use
```
Error: Port 8080 already in use
Solution: Change server.port in application.properties
```

---

## Summary

This project demonstrates:

- **Q1**: JDBC programming with transaction management (commit/rollback)
- **Q2**: Spring Boot REST controller with simple endpoint
- **Q3**: Spring Data JPA with MySQL/PostgreSQL integration and automatic table creation
- **Advanced**: Connection pooling, validation, error handling, and production-best practices

All code is well-documented, follows Spring Boot conventions, and is ready for production use.

---

## Additional Resources

- Spring Boot Documentation: https://spring.io/projects/spring-boot
- Spring Data JPA: https://spring.io/projects/spring-data-jpa
- PostgreSQL: https://www.postgresql.org/docs/
- HikariCP: https://github.com/brettwooldridge/HikariCP
- RESTful API Best Practices: https://restfulapi.net/

---

**Last Updated:** April 2026
**Java Version:** 17+
**Spring Boot Version:** 4.0.6
**Database:** PostgreSQL
