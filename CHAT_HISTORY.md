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
- used promt engineering techniques to ensure the AI strictly followed the requirements without adding extra features or assumptions.

## actual promt
- You are a senior backend engineer helping me implement a production-grade Java service.

IMPORTANT RULES
1. Carefully read the complete requirement document provided in the README.md file in this repository.
2. DO NOT assume any requirements that are not explicitly mentioned in the README.
3. DO NOT hallucinate additional features.
4. If something is unclear, ask for clarification instead of guessing.
5. Follow all relationships, constraints, and validations exactly as described in the README.

PROJECT GOAL
Build a production-ready backend service strictly based on the requirements defined in README.md.

TECH STACK
Use the following stack unless the README specifies otherwise:
- Java 21
- Spring Boot
- Spring Data JPA
- Maven
- H2/PostgreSQL compatible configuration
- JUnit + Mockito for testing
- Lombok (optional but preferred)
- OpenAPI/Swagger for API documentation

ARCHITECTURE REQUIREMENTS
Follow clean and maintainable architecture:

controller/
service/
repository/
entity/
dto/
mapper/
exception/
validation/
config/

Ensure:
- Clear separation of concerns
- SOLID principles
- Clean, readable, maintainable code
- Production-grade structure

API REQUIREMENTS
Generate REST APIs exactly according to the requirements from README:
- Correct HTTP methods
- Proper status codes
- Request/Response DTOs
- Input validation
- Pagination support if required
- Filtering and search if specified

DATA MODEL
Implement entity relationships exactly as described in the README.
Use appropriate JPA annotations for relationships and constraints.

VALIDATIONS
Include:
- Request validation
- Business validation
- Referential integrity checks
- Edge case handling
- Proper exception handling

ERROR HANDLING
Create a global exception handler that returns structured error responses.

TESTING
Write unit tests for:
- Services
- Controllers

Ensure:
- High test coverage
- Mock dependencies properly
- Test edge cases

DOCUMENTATION
Generate:
- Swagger/OpenAPI documentation
- README setup instructions
- PROJECT_STRUCTURE.md explaining architecture

CODE QUALITY
Ensure:
- Production-level naming conventions
- No duplicate code
- Proper logging
- Clean modular design
- No unnecessary complexity

WORKFLOW
1. First analyze the README requirements.
2. Summarize the system design before writing code.
3. Define entities and relationships.
4. Design API contracts.
5. Implement the project step-by-step.
6. Add validations, exception handling, and tests.
7. Ensure the final solution strictly matches the requirements.

Always prioritize correctness and requirement compliance over adding extra features.