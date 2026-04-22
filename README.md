# Order Service

## Overview

The Order Service manages the complete ticket purchase lifecycle for buyers on the event ticketing platform. It validates tier availability and quantity limits, creates orders with snapshotted event data, integrates with Stripe for payment processing, atomically decrements inventory upon successful payment, and provides buyers access to their order history.

## Technology Stack

- **Language**: Java 21
- **Framework**: Spring Boot 3.2.5
- **Build Tool**: Maven 3.9.6
- **Database**: PostgreSQL 15
- **Migration**: Flyway
- **Payment**: Stripe Java SDK
- **Testing**: JUnit 5

## Architecture

The service follows a layered architecture:

```
Controller → Service → Repository → Database
```

- **Controllers**: Handle HTTP requests, JWT validation, and response construction
- **Services**: Contain business logic for order creation, validation, and webhook processing
- **Repositories**: Data access layer for orders schema and cross-schema queries
- **Utilities**: JWT parsing, audit logging, Stripe integration

## Features

### Phase 1: Create Order and Initiate Payment
- **Endpoint**: `POST /api/orders`
- **Authentication**: JWT with BUYER role
- **Flow**:
  1. Validate tier availability and status (ACTIVE)
  2. Validate event status (PUBLISHED)
  3. Check quantity limits (maxPerOrder) and inventory
  4. Create order and order items with snapshotted data
  5. Generate Stripe Checkout session
  6. Return order ID and Stripe checkout URL

### Phase 2: Retrieve Order History
- **Endpoint**: `GET /api/orders/my`
- **Authentication**: JWT with BUYER role
- **Flow**:
  1. Query confirmed orders for authenticated buyer
  2. Return paginated list sorted by event date

### Phase 3: Retrieve Single Order
- **Endpoint**: `GET /api/orders/{id}`
- **Authentication**: JWT with BUYER role
- **Flow**:
  1. Validate order ownership
  2. Return order details with all items

### Phase 4: Process Payment Webhook
- **Endpoint**: `POST /api/payments/webhook`
- **Authentication**: Stripe signature verification
- **Flow**:
  1. Verify webhook signature
  2. Parse Stripe event (checkout.session.completed)
  3. Atomically decrement inventory (prevents overselling)
  4. Update order status (CONFIRMED or FAILED)

## Database Schema

### orders.orders
- `id` (UUID, PK)
- `buyer_id` (UUID, FK to auth.users)
- `status` (VARCHAR - PENDING, CONFIRMED, FAILED)
- `total_amount` (NUMERIC)
- `stripe_session_id` (TEXT)
- `created_at`, `updated_at` (TIMESTAMP)

### orders.order_items
- `id` (UUID, PK)
- `order_id` (UUID, FK to orders.orders)
- `tier_id` (UUID, FK to events.ticket_tiers - app-enforced)
- `tier_name`, `event_title`, `event_date`, `unit_price` (Snapshotted values)
- `quantity` (INTEGER)
- `created_at` (TIMESTAMP)

## Setup and Installation

### Prerequisites

- Java 21
- Docker and Docker Compose
- Stripe account (test mode)

### Environment Variables

Create a `.env` file based on `.env.example`:

```bash
cp .env.example .env
```

Fill in the required values:
- `STRIPE_SECRET_KEY`: Your Stripe secret key (sk_test_...)
- `STRIPE_WEBHOOK_SECRET`: Your Stripe webhook secret (whsec_...)
- `JWT_SECRET`: A secure 256-bit secret key (minimum 32 characters)

### Local Development

1. **Start PostgreSQL**:
   ```bash
   docker-compose up postgres -d
   ```

2. **Run the application**:
   ```bash
   ./mvnw bootRun
   ```

3. **Access the service**:
   - API: http://localhost:8080/api/orders
   - Health: http://localhost:8080/health
   - Swagger: Place `swagger/order-service-openapi.yaml` in Swagger UI

### Docker Deployment

1. **Build and start all services**:
   ```bash
   docker-compose up --build
   ```

2. **Verify services**:
   ```bash
   curl http://localhost:8080/health
   ```

3. **View logs**:
   ```bash
   docker-compose logs -f order-service
   ```

### Stripe Webhook Testing

For local webhook testing, use the Stripe CLI:

```bash
stripe listen --forward-to localhost:8080/api/payments/webhook
```

This will provide a webhook secret (whsec_...) to use in your `.env` file.

## API Endpoints

| Endpoint | Method | Auth | Description |
|----------|--------|------|-------------|
| `/api/orders` | POST | BUYER | Create order and initiate payment |
| `/api/orders/my` | GET | BUYER | Retrieve order history (paginated) |
| `/api/orders/{id}` | GET | BUYER | Retrieve single order detail |
| `/api/payments/webhook` | POST | Stripe Sig | Process payment webhook |
| `/health` | GET | None | Health check |
| `/health/live` | GET | None | Liveness probe |
| `/health/ready` | GET | None | Readiness probe |

## Configuration

### Application Properties

Located in `src/main/resources/application.properties`:

- **Server**: Port configuration
- **Database**: PostgreSQL connection settings
- **Flyway**: Database migration configuration
- **Stripe**: API keys and webhook secrets
- **JWT**: Secret key and token expiry
- **Logging**: MDC-based tracing with automatic traceId
- **CORS**: Allowed origins for API access

## Business Rules

1. **Atomic Inventory Decrement**: Inventory is only decremented after successful payment via atomic SQL update
2. **Data Snapshotting**: Tier and event details are captured at purchase time (immutable historical records)
3. **Order Status Lifecycle**: PENDING → CONFIRMED (payment success) or FAILED (payment/inventory failure)
4. **Quantity Limits**: Enforced via tier's `maxPerOrder` field
5. **Idempotency**: Webhook processing prevents duplicate events using `stripe_event_id`
6. **Authorization**: Buyers can only access their own orders

## Error Handling

All exceptions are handled by `GlobalExceptionHandler` (@ControllerAdvice):

- **HTTP 400**: Validation failures, invalid requests
- **HTTP 401**: Missing/invalid JWT, unauthorized access
- **HTTP 403**: Order ownership mismatch
- **HTTP 404**: Resource not found
- **HTTP 409**: Conflicts (sold out, inactive tier)
- **HTTP 500**: Server errors, Stripe API failures

Error responses include:
- `errorCode`: Machine-readable error identifier
- `message`: Human-readable description
- `timestamp`: Error occurrence time
- `traceId`: Request trace ID (automatically captured via MDC)

## Audit Logging

Dedicated `AuditService` logs critical events:

- Order created
- Stripe session created
- Webhook received
- Order confirmed/failed
- Inventory decremented
- Security warnings (invalid signatures)

All logs include masked sensitive data and automatic traceId via MDC/SLF4J.

## Testing

### Run Tests

```bash
./mvnw test
```

### Test Coverage

- **Instructions**: ≥ 90%
- **Branches**: ≥ 90%
- **Lines**: ≥ 95%
- **Methods**: ≥ 95%
- **Classes**: 100%

Tests are organized by chunks:
1. DTOs / Data Types
2. Entities / Domain Models
3. Utilities / Helpers
4. Exception / Error Handling
5. Controller / API Layer
6. Business / Service Layer
7. Data Access / Repository
8. Configuration / Setup
9. Deployment / Containerization
10. Full-layer Integration

## OpenAPI Specification

Located in `swagger/order-service-openapi.yaml`:

- Complete API documentation
- Request/response schemas with examples
- Error responses for all status codes
- Authentication requirements
- Health endpoints

## Security

- **JWT Authentication**: All order endpoints require valid BUYER role token
- **Stripe Webhook Verification**: Signature validation on all webhook requests
- **CORS**: Configured for User App origin only
- **MDC Tracing**: Automatic traceId propagation (no manual logging)
- **Data Masking**: Sensitive information masked in audit logs

## Monitoring

### Key Metrics

- Order creation rate
- Order confirmation rate
- Order failure rate
- Webhook processing time
- Inventory decrement failures

### Health Checks

- `/health`: Overall service health
- `/health/live`: Kubernetes liveness probe
- `/health/ready`: Kubernetes readiness probe

## Edge Cases Handled

- Concurrent oversell prevention (atomic inventory updates)
- Duplicate webhook processing (idempotency)
- Webhook arrives before order creation completes
- Payment succeeds but inventory depleted
- Tier deleted after order created
- Event cancelled after order created
- Unauthorized order access attempts
- Quantity exceeds maxPerOrder
- Stripe API failures with transaction rollback

## Cross-Schema References

The Order Service queries data from other schemas:

- `auth.users`: Buyer authentication (app-enforced FK)
- `events.events`: Event details for snapshotting
- `events.ticket_tiers`: Tier availability and pricing

All cross-schema references are validated at the application layer (no database-level FKs).

## Out of Scope (v1)

- Refund processing
- Order cancellation
- Order modification
- Email notifications
- PDF ticket generation
- QR code generation
- Bulk orders
- Promo codes/discounts
- Partial payments
- Order expiry
- Admin order management

## Support

For issues or questions:
- Email: support@ticketing.com
- Documentation: `swagger/order-service-openapi.yaml`

## License

Proprietary - Ticketing Platform Team
