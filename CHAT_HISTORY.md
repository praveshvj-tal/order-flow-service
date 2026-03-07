# CHAT_HISTORY

This document summarizes the design journey with the AI assistant.

## Key decisions
- Implemented explicit state-machine validators (`OrderStateMachine`, `ReturnStateMachine`) to enforce the exact transitions from the assignment.
- Persisted state-history rows (`OrderStateHistory`, `ReturnStateHistory`) with `entityId`, `previousState`, `newState`, `timestamp` for auditing.
- Used Spring `@Async` for background processing as allowed by requirements:
  - Order -> `SHIPPED`: async invoice generation (dummy PDF file) and simulated email.
  - Return -> `COMPLETED`: async call to a mock payment gateway endpoint.
- Used H2 by default for fast local runs, and PostgreSQL via `docker-compose.yml` for validation.

## How AI was used
- Converted the PRD text into a concrete architecture and package layout.
- Implemented the code iteratively with validation gates (compile + unit tests).
- Kept the API surface limited to the explicitly requested endpoints.

