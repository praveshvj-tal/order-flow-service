# Workflow Design

## Order State Machine

Allowed states and transitions:

```
PENDING_PAYMENT -> PAID -> PROCESSING_IN_WAREHOUSE -> SHIPPED -> DELIVERED

Cancellation:
PENDING_PAYMENT -> CANCELLED
PAID -> CANCELLED
```

Enforcement: `state_machine/OrderStateMachine#validateTransition(from,to)`.

Async side-effect:
- When transitioning into `SHIPPED`, `background_jobs/InvoiceJobService` is triggered asynchronously to generate a dummy PDF and simulate email.

## Return State Machine

A return can only be initiated for an Order in `DELIVERED` state.

Allowed states and transitions:

```
REQUESTED -> APPROVED / REJECTED
APPROVED -> IN_TRANSIT -> RECEIVED -> COMPLETED
REJECTED (terminal)
COMPLETED (terminal)
```

Enforcement: `state_machine/ReturnStateMachine#validateTransition(from,to)`.

Async side-effect:
- When transitioning into `COMPLETED`, `background_jobs/RefundJobService` is triggered asynchronously to call a mock payment gateway endpoint.

## State History / Audit

Two audit tables are used:
- `order_state_history`
- `return_state_history`

Each history row stores:
- `entityId`
- `previousState`
- `newState`
- `timestamp`

History is written on every state change.

## Database Schema (high-level)

- `orders(id PK, state, created_at)`
- `return_requests(id PK, order_id FK -> orders.id, state, created_at)`
- `order_state_history(id PK, entity_id, previous_state, new_state, timestamp)`
- `return_state_history(id PK, entity_id, previous_state, new_state, timestamp)`

Relationships:
- `return_requests.order_id` is a many-to-one to `orders`.

