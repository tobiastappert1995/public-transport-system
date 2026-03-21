# Public Transport System

A Spring Boot backend project that models a small public transport domain and exposes a focused REST API for:

- searching trip executions
- creating tickets for a concrete trip execution

The project is intentionally designed as a compact backend showcase with a focus on:

- clear domain modeling
- explicit design decisions
- layered architecture
- integration-tested use cases

---

## Tech Stack

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- PostgreSQL
- H2 (tests)
- Maven

---

## Project Goal

The main use case is **trip search by time, location, and price**.  
A second use case is **ticket creation for one concrete trip execution**.

This project is meant to demonstrate more than simple CRUD by modeling a small but meaningful domain.

---

## Core Features

- Search trip executions by:
    - time range
    - stop location
    - maximum ticket price
- Return route information including travel paths and ordered stops
- Create tickets for one specific trip execution
- Calculate ticket prices depending on the trip day type
- Generate demo data for local testing and presentation
- Provide a simple root endpoint for quick status checks

---

## Architecture

The project is structured into four layers:

### `api`

REST controllers, request/response DTOs, validation, and error handling.

### `application`

Use-case orchestration such as trip search and ticket creation.

### `domain`

Core business model with entities, value objects, and pricing strategies.

### `infrastructure`

Persistence concerns such as Spring Data JPA repositories.

---

## Selected Design Decisions

### `Trip` vs `TripExecution`

A `Trip` represents the reusable definition of a service.  
A `TripExecution` represents one execution at a specific time.

Tickets are therefore bought for a `TripExecution`, not for a generic `Trip`.

### Multiple `TravelPaths` per `Trip`

The customer specification states that trips consist of at least one travel path.  
For that reason, a `Trip` is modeled with one or more `TravelPaths` instead of a single fixed route.

### Slim Trip Search Response

The search response only returns the information needed to choose a trip:

- trip execution id
- execution time
- trip name
- ticket price
- currency
- travel paths with ordered stops

Operational details that are not useful for the client use case are intentionally left out.

### Price Filtering Uses the Calculated Ticket Price

The maximum price filter is applied to the final ticket price, not only to the persisted base fare.

This matches what a client would expect from a price-based search.

### `Money` as a Value Object

Price and currency are modeled together as a `Money` value object instead of using a raw `BigDecimal` throughout the
codebase.

### Root Endpoint for Simple Status Checks

The application provides a simple root endpoint at `/` for quick manual checks.

It is intentionally lightweight and returns a small response that confirms the application is running.

---

## Running the Project

### Prerequisites

- Java 21
- Maven
- PostgreSQL

### Environment Variables

The application expects:

- `DB_USERNAME`
- `DB_PASSWORD`

### Start the application

```bash
export DB_USERNAME=${DB_USERNAME}
export DB_PASSWORD=${DB_PASSWORD}
./mvnw spring-boot:run
```

---

## Quick Start

1. Start the application
2. Call the root endpoint
3. Create demo data
4. Run a trip search
5. Create a ticket

---

## API Overview

- `GET /`
- `POST /api/internal/demo-data`
- `DELETE /api/internal/demo-data`
- `POST /api/trips/search`
- `POST /api/tickets`

---

## Root Endpoint Example

The root endpoint returns a small status message for quick manual checks.

Example controller:

```java

@RestController
public class RootController {

    @GetMapping("/")
    public String status() {
        return "Public Transport System is running";
    }
}
```

Example request:

```bash
curl http://localhost:8080/
```

Example response:

```text
Public Transport System is running
```

---

## API Usage Examples

### 1. Create Demo Data

```bash
curl -X POST http://localhost:8080/api/internal/demo-data \
  -H "Content-Type: application/json" \
  -d '{
    "tripName": "Postman Demo Line",
    "dayType": "WEEKDAY",
    "fareAmount": 4.5,
    "currency": "EUR",
    "startExecutionTime": "2025-09-10T09:00:00",
    "numberOfExecutions": 2,
    "executionIntervalMinutes": 30,
    "busesPerExecution": 1,
    "busNumberPrefix": "PM-",
    "stopNames": ["Stop A", "Stop B", "Stop C"],
    "locationPrefix": "Aachen"
  }'
```

### 2. Delete Demo Data

```bash
curl -X DELETE http://localhost:8080/api/internal/demo-data
```

### 3. Trip Search

#### Happy Path

```bash
curl -X POST http://localhost:8080/api/trips/search \
  -H "Content-Type: application/json" \
  -d '{
    "from": "2025-09-10T08:00:00",
    "until": "2025-09-10T12:00:00",
    "location": "Aachen",
    "maxPrice": 5.0
  }'
```

Example response:

```json
[
  {
    "tripExecutionId": 44,
    "executionTime": "2025-09-10T09:00:00",
    "tripName": "Postman Demo Line",
    "ticketPrice": 4.50,
    "currency": "EUR",
    "travelPaths": [
      {
        "name": "Postman Demo Line Path1",
        "departureStop": "Stop A",
        "destinationStop": "Stop C",
        "stops": [
          {
            "order": 1,
            "stopName": "Stop A",
            "location": "Aachen"
          },
          {
            "order": 2,
            "stopName": "Stop B",
            "location": "Aachen"
          },
          {
            "order": 3,
            "stopName": "Stop C",
            "location": "Aachen"
          }
        ]
      },
      {
        "name": "Postman Demo Line Path2",
        "departureStop": "Stop A",
        "destinationStop": "Stop C",
        "stops": [
          {
            "order": 1,
            "stopName": "Stop A",
            "location": "Aachen"
          },
          {
            "order": 2,
            "stopName": "Stop B",
            "location": "Aachen"
          },
          {
            "order": 3,
            "stopName": "Stop C",
            "location": "Aachen"
          }
        ]
      }
    ]
  },
  {
    "tripExecutionId": 45,
    "executionTime": "2025-09-10T09:30:00",
    "tripName": "Postman Demo Line",
    "ticketPrice": 4.50,
    "currency": "EUR",
    "travelPaths": [
      {
        "name": "Postman Demo Line Path1",
        "departureStop": "Stop A",
        "destinationStop": "Stop C",
        "stops": [
          {
            "order": 1,
            "stopName": "Stop A",
            "location": "Aachen"
          },
          {
            "order": 2,
            "stopName": "Stop B",
            "location": "Aachen"
          },
          {
            "order": 3,
            "stopName": "Stop C",
            "location": "Aachen"
          }
        ]
      },
      {
        "name": "Postman Demo Line Path2",
        "departureStop": "Stop A",
        "destinationStop": "Stop C",
        "stops": [
          {
            "order": 1,
            "stopName": "Stop A",
            "location": "Aachen"
          },
          {
            "order": 2,
            "stopName": "Stop B",
            "location": "Aachen"
          },
          {
            "order": 3,
            "stopName": "Stop C",
            "location": "Aachen"
          }
        ]
      }
    ]
  }
]
```

#### Invalid Time Range

```bash
curl -X POST http://localhost:8080/api/trips/search \
  -H "Content-Type: application/json" \
  -d '{
    "from": "2025-09-10T12:00:00",
    "until": "2025-09-10T08:00:00",
    "location": "Aachen",
    "maxPrice": 5.0
  }'
```

Example response:

```json
{
  "timestamp": "2026-03-28T11:55:09.952837600Z",
  "status": 400,
  "error": "Bad Request",
  "message": "from must not be after until"
}
```

#### Invalid Max Price

```bash
curl -X POST http://localhost:8080/api/trips/search \
  -H "Content-Type: application/json" \
  -d '{
    "from": "2025-09-10T08:00:00",
    "until": "2025-09-10T12:00:00",
    "location": "Aachen",
    "maxPrice": -1
  }'
```

Example response:

```json
{
  "timestamp": "2026-03-28T11:55:24.814140Z",
  "status": 400,
  "error": "Bad Request",
  "message": "maxPrice must be greater than or equal to 0"
}
```

### 4. Create Ticket

#### Validation Error

```bash
curl -X POST http://localhost:8080/api/tickets \
  -H "Content-Type: application/json" \
  -d '{
    "tripExecutionId": 44
  }'
```

Example response:

```json
{
  "timestamp": "2026-03-28T11:55:34.516497600Z",
  "status": 400,
  "error": "Bad Request",
  "message": "travelerId must not be null"
}
```

#### Invalid Request Example

```bash
curl -X POST http://localhost:8080/api/tickets \
  -H "Content-Type: application/json" \
  -d '{
    "tripExecutionId": 45
  }'
```

Example response:

```json
{
  "timestamp": "2026-03-28T11:55:46.574966800Z",
  "status": 400,
  "error": "Bad Request",
  "message": "travelerId must not be null"
}
```

#### Happy Path

This request requires:

- a valid existing traveler id
- a valid `tripExecutionId`

```bash
curl -X POST http://localhost:8080/api/tickets \
  -H "Content-Type: application/json" \
  -d '{
    "travelerId": 1,
    "tripExecutionId": 44
  }'
```

---
