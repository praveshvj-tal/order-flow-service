# SYSTEM_EXPLANATION

## 1) Project Overview

This system is a small backend service for an e-commerce platform.

It helps you:
- **Create an order**
- **Move an order through its normal steps** (like “paid”, “shipped”, “delivered”)
- **Cancel an order** (but only at certain times)
- **Create a return request** for an order (but only after it’s delivered)
- **Move a return through its steps** (approved, in transit, received, completed)

It also keeps a **history log** of every state change, so you can later answer questions like:
- “When did this order get shipped?”
- “Who moved it from PAID to PROCESSING?” (Note: this project tracks *what* changed and *when*, not the user.)


## 2) High Level Flow (Order Lifecycle)

An order can only move through these steps:

```text
PENDING_PAYMENT → PAID → PROCESSING_IN_WAREHOUSE → SHIPPED → DELIVERED
```

Think of these as labels that describe where the order currently is.

### When cancellation is possible
Cancellation is only allowed in two situations:

```text
PENDING_PAYMENT → CANCELLED
PAID → CANCELLED
```

If the order is already being prepared, shipped, or delivered, it **cannot** be cancelled.

### The system enforces this automatically
If you try to skip steps (example: go from `PENDING_PAYMENT` straight to `SHIPPED`), the API will reject the request with a **400 Bad Request**.


## 3) Return Workflow

A return request has its own steps:

```text
REQUESTED → APPROVED / REJECTED → IN_TRANSIT → RECEIVED → COMPLETED
```

### Important rule: returns can only start after delivery
A return request can only be created if the order is in:

```text
DELIVERED
```

If you try to start a return for an order that is not delivered, the API returns **400 Bad Request** with this message:

- `Return can only be initiated for DELIVERED orders`

### Approved vs Rejected
- If a return is **REJECTED**, it stops there (no more steps).
- If it is **APPROVED**, it can continue through shipping back and completion.


## 4) Simple Real-Life Example (Story)

Here’s a simple story of how the system is used.

### Order story
1. **Customer places an order**
   - The system creates it as: `PENDING_PAYMENT`
2. **Customer pays**
   - The order becomes: `PAID`
3. **Warehouse starts working on it**
   - The order becomes: `PROCESSING_IN_WAREHOUSE`
4. **Item is shipped**
   - The order becomes: `SHIPPED`
   - At this moment, the system starts a background task to create a **dummy invoice PDF**
5. **Item is delivered**
   - The order becomes: `DELIVERED`

### Return story
6. **Customer asks for a return**
   - A return request is created as: `REQUESTED`
7. **Store approves the return**
   - The return becomes: `APPROVED`
8. **Customer ships it back**
   - The return becomes: `IN_TRANSIT`
9. **Store receives the item**
   - The return becomes: `RECEIVED`
10. **Return is completed**
   - The return becomes: `COMPLETED`
   - At this moment, the system starts a background task to process a **refund** by calling a mock payment gateway API.


## 5) System Components (What each part does)

This project is split into a few simple layers:

### Controller – “the door to the system”
- These classes define the **HTTP API endpoints**.
- Example: `OrderController` handles `/orders/...`

### Service – “the brain”
- These classes contain the rules.
- Example:
  - `OrderService` checks if an order is allowed to change from one state to another.
  - `ReturnService` checks the return rules (like “only delivered orders can start a return”).

### Repository – “talks to the database”
- These classes save and load data from the database.
- Example: `OrderRepository`, `ReturnRequestRepository`.

### State Machine – “the traffic rules”
- These classes contain the allowed state transitions.
- `OrderStateMachine` rejects invalid order changes.
- `ReturnStateMachine` rejects invalid return changes.

### Background Jobs – “slow work done later”
- These tasks run **asynchronously** (in the background).
- They don’t make the API request wait.


## 6) Background Jobs (Async tasks)

### 6.1 Invoice generation when an order ships
When an order changes to `SHIPPED`, the system triggers:
- `InvoiceJobService.generateAndEmailInvoice(orderId)`

What it does:
- Creates a dummy PDF file named like:
  - `./invoices/invoice-order-<orderId>.pdf`
- Logs a message that it “sent an email” (simulation only)

### 6.2 Refund processing when a return completes
When a return changes to `COMPLETED`, the system triggers:
- `RefundJobService.processRefund(returnRequestId)`

What it does:
- Calls this internal mock API endpoint:
  - `POST /mock-payment-gateway/refunds`
- The mock endpoint always returns a simple success response.

### Why async?
Because generating files and calling other services can take time.
Running them in the background means:
- API requests return faster
- Users don’t have to wait for “slow tasks”


## 7) Database Overview (Simple view)

The system stores data in a database using these main tables:

### `orders`
Stores the current state of each order.
- `id`
- `state` (like `PAID`, `SHIPPED`)
- `created_at`

### `return_requests`
Stores the current state of each return and links it to an order.
- `id`
- `order_id` (points to `orders.id`)
- `state` (like `REQUESTED`, `COMPLETED`)
- `created_at`

### `order_state_history`
Stores every order state change (audit log).
- `entity_id` (the order id)
- `previous_state`
- `new_state`
- `timestamp`

### `return_state_history`
Stores every return state change (audit log).
- `entity_id` (the return id)
- `previous_state`
- `new_state`
- `timestamp`

### Relationship (simple)
- One **Order** can have many **Return Requests** (in the code it’s stored as “ReturnRequest has an Order”).


## 8) How to Test Using Swagger (step-by-step)

Open Swagger UI:
- `http://localhost:8080/swagger-ui.html`

### Step A — Create an Order
1. Call: `POST /orders`
2. Copy the returned `id`

### Step B — Move order through states
Use: `PUT /orders/{orderId}/state` with a body like:

```json
{ "newState": "PAID" }
```

Then call it again in order:
- `PROCESSING_IN_WAREHOUSE`
- `SHIPPED`  (this triggers invoice generation)
- `DELIVERED`

### Optional — Cancel an order
Use: `POST /orders/{orderId}/cancel`

Note:
- This only works if the current order state is `PENDING_PAYMENT` or `PAID`.

### Step C — Initiate a Return (only after delivered)
1. Call: `POST /returns?orderId=<orderId>`
2. Copy the returned return `id` (this is `returnId`)

### Step D — Approve and complete the return
Call these in order:
- `POST /returns/{returnId}/approve`
- `POST /returns/{returnId}/in-transit`
- `POST /returns/{returnId}/received`
- `POST /returns/{returnId}/complete` (this triggers refund processing)

### Check history logs
You can view state history any time:
- `GET /orders/{orderId}/history`
- `GET /returns/{returnId}/history`


## 9) Example Full Workflow (end-to-end)

This is the full journey from order to refund:

```text
1) Create order
   POST /orders
   → orderId = 100

2) Pay
   PUT /orders/100/state  {newState: PAID}

3) Warehouse processing
   PUT /orders/100/state  {newState: PROCESSING_IN_WAREHOUSE}

4) Ship
   PUT /orders/100/state  {newState: SHIPPED}
   → background: invoice file is written to ./invoices

5) Deliver
   PUT /orders/100/state  {newState: DELIVERED}

6) Start return
   POST /returns?orderId=100
   → returnId = 55

7) Approve
   POST /returns/55/approve

8) In transit
   POST /returns/55/in-transit

9) Received
   POST /returns/55/received

10) Complete
    POST /returns/55/complete
    → background: refund job calls POST /mock-payment-gateway/refunds
```

That’s it: the system moves items through clear steps, prevents invalid jumps, and records every change for auditing.

