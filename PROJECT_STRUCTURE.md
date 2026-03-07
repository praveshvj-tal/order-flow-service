# Project Structure

This project follows the required package structure from the assignment.

```
src/main/java/com/articurated/orderflow
  controller/        REST controllers (Order + Return + mock gateway)
  service/           Business services orchestrating state transitions and persistence
  repository/        Spring Data JPA repositories
  entity/            JPA entities + enums (Order, ReturnRequest, history tables)
  dto/               API request/response DTOs
  mapper/            Converters between entities and DTOs
  state_machine/     Transition validation for Order and Return state machines
  background_jobs/   Asynchronous invoice generation + refund processing integration
  exception/         Domain/API exceptions + global exception handler
  config/            (Reserved for configuration beans if needed)
```

Key modules:
- `state_machine/*StateMachine`: defines allowed transitions and rejects illegal ones.
- `*StateHistory` entities: store every transition for audit with `entityId`, `previousState`, `newState`, `timestamp`.
- `InvoiceJobService` and `RefundJobService`: run asynchronously so request threads aren’t blocked.

