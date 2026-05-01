#!/bin/bash
# Quick Testing Script for Advanced Java Assignment
# This script contains all test commands for Q1, Q2, and Q3

echo "=========================================="
echo "Advanced Java Assignment - Quick Tests"
echo "=========================================="
echo ""

# Color codes for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

BASE_URL="http://localhost:8080/api"

echo -e "${BLUE}=== Q2: Simple Registration Test ===${NC}"
echo "Testing POST /register endpoint"
echo ""

echo "Test: Register user 'John Doe' with email 'john@example.com'"
curl -X POST "${BASE_URL}/register?name=John%20Doe&email=john@example.com"
echo -e "\n"

echo "Test: Register user with empty name (should fail)"
curl -X POST "${BASE_URL}/register?name=&email=test@example.com"
echo -e "\n\n"

echo -e "${BLUE}=== Q1: Flight Booking Tests ===${NC}"
echo "Testing POST /book endpoint with JDBC transactions"
echo ""

echo "Test 1: Successful booking - 10 seats from flight 1"
curl -X POST "${BASE_URL}/book?flightId=1&passengerName=Alice%20Smith&seatsRequested=10"
echo -e "\n"

echo "Test 2: Booking insufficient seats (should fail with rollback)"
curl -X POST "${BASE_URL}/book?flightId=1&passengerName=Bob%20Johnson&seatsRequested=500"
echo -e "\n"

echo "Test 3: Booking from invalid flight (should fail)"
curl -X POST "${BASE_URL}/book?flightId=999&passengerName=Charlie%20Brown&seatsRequested=5"
echo -e "\n"

echo "Test 4: Valid booking from flight 2"
curl -X POST "${BASE_URL}/book?flightId=2&passengerName=Diana%20Prince&seatsRequested=15"
echo -e "\n\n"

echo -e "${BLUE}=== Q3: Student Registration Tests ===${NC}"
echo "Testing POST /student/register endpoint with JPA"
echo ""

echo "Test 1: Register a valid student"
curl -X POST "${BASE_URL}/student/register" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Raj Kumar",
    "email": "raj@example.com",
    "course": "B.Tech Computer Science",
    "phone": "9876543210"
  }'
echo -e "\n"

echo "Test 2: Register another student"
curl -X POST "${BASE_URL}/student/register" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Priya Sharma",
    "email": "priya@example.com",
    "course": "B.Tech Electronics",
    "phone": "9876543211"
  }'
echo -e "\n"

echo "Test 3: Get student by ID (assuming ID=1)"
curl -X GET "${BASE_URL}/student/1"
echo -e "\n"

echo "Test 4: Update student (assuming ID=1)"
curl -X PUT "${BASE_URL}/student/1" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Raj Kumar Updated",
    "course": "M.Tech Computer Science"
  }'
echo -e "\n"

echo "Test 5: Delete student (assuming ID=1)"
curl -X DELETE "${BASE_URL}/student/1"
echo -e "\n\n"

echo -e "${GREEN}=========================================="
echo "All tests completed!"
echo "==========================================${NC}"
