# order-flow-service

OrderFlow is a backend Order & Returns Management System for managing the lifecycle of e-commerce orders and return requests.

This repository implements the exact requirements described in the assignment:
- Order state machine with strict transitions and cancellation rules
- Return state machine with strict transitions, only initiable for delivered orders
- Audit history tracking for all state transitions
- Asynchronous background jobs for invoice generation and refund processing
- REST APIs with Swagger/OpenAPI documentation

## Tech stack
- Java 21
- Spring Boot
- Spring Data JPA
- Maven
- H2 (default) or PostgreSQL (Docker)
- JUnit 5 + Mockito
- Swagger/OpenAPI (springdoc)

## Run locally (H2)

```bash
mvn test
mvn spring-boot:run
```

Then open:
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

Invoices (dummy PDFs) are written to `./invoices` when an Order transitions to `SHIPPED`.

## Run with Docker Compose (PostgreSQL)

```bash
docker compose up --build
```

The service will be available on http://localhost:8080.

## API summary
See `API-SPECIFICATION.yml` for the endpoint list.

## Required documentation
- `PROJECT_STRUCTURE.md`
- `WORKFLOW_DESIGN.md`
- `API-SPECIFICATION.yml`
- `CHAT_HISTORY.md`
- `docker-compose.yml`

---

(Original assignment requirements kept in earlier repository history / prompt context; this README focuses on run instructions.)
