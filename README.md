# Configuration Change Tracker

This project implements a simple **event-based configuration change tracker**.  
Its responsibility is to record, store, and retrieve configuration change events coming from an external configuration management system.

The service **does not manage configuration state itself**. It only tracks changes as immutable events for auditing, troubleshooting, or monitoring purposes.

---

## Core Concept

A configuration change is treated as an **event**, not as a state mutation.

Each event describes:
- what type of rule was affected
- what kind of change occurred (CREATE / UPDATE / DELETE)
- who initiated the change
- the configuration values *before* and/or *after* the change
- whether the change is considered critical

The tracker does **not**:
- validate whether the rule actually exists
- reconcile current configuration state
- enforce consistency between events

These responsibilities are assumed to belong to an external configuration management system.

---

## Scope & Assumptions

This service is intentionally scoped with the following assumptions:

- Configuration is managed externally (e.g. a config manager, admin UI, CI/CD pipeline)
- The tracker only records **change events**
- Events are **append-only** and immutable once stored
- `before` and `after` values are stored as opaque payloads (JSON strings)
- No deduplication or conflict resolution is performed

---

## API Overview

### Create configuration change
```
POST /config-change
```

Creates a new configuration change event.

### Retrieve configuration change by ID
```
GET /config-change/{id}
```


Returns a single change event by its identifier.

### List configuration changes
```
GET /config-change
```


Optional query parameters:
- `ruleType`
- `changeType`
- `from` (timestamp in format YYYY-MM-DDThh:mm:ssZ)
- `to` (timestamp in format YYYY-MM-DDThh:mm:ssZ)

Filters can be combined.

---

## Validation Rules

Validation rules depend on the type of change:

- **CREATE**
  - `after` is required
  - `before` must be absent

- **UPDATE**
  - both `before` and `after` are required

- **DELETE**
  - `before` is required
  - `after` must be absent

Additional validation:
- `ruleType`, `changeType`, and `changedBy` are mandatory
- `changedBy` must not be blank
- Invalid input results in `400 Bad Request`
- Non-existent IDs return `404 Not Found`

---

## Critical Change Notification

Changes marked as `critical = true` trigger a simulated external integration.

For this exercise, the integration is implemented as a logging-based notifier.  
The notifier is designed behind an interface to allow easy replacement with a real external system (e.g. messaging, alerting, webhook).

---

## Persistence

All configuration changes are stored **in memory**.

---

## Health Check
```
GET /actuator/health
```


Provides a basic health check endpoint.

---

## Testing Strategy

The project uses a layered testing approach:

- **Unit tests**
  - validation logic
  - service-level behavior
  - notifier behavior

- **Integration tests**
  - HTTP request/response contract
  - validation via REST API
  - filtering and retrieval behavior

Integration tests use `MockMvc` to exercise the full Spring MVC stack without starting a real HTTP server.  

---

## Technology Stack

- Java 21
- Spring Boot 3.x
- Spring Web
- Bean Validation (Jakarta Validation)
- JUnit 5
- MockMvc

---

## How to Run

### Run application
```
./mvnw spring-boot:run
```


The application will start on the default port `8080`.

---

## Notes

This project is intentionally kept small and focused to demonstrate:
- clean API design
- explicit assumptions
- validation logic
- testability
- clear separation of responsibilities

