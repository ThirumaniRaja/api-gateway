# Mini API Gateway (Spring Boot + MongoDB)

This project implements a mini API Gateway with:
- JWT authentication
- Token Bucket rate limiting (per user + IP)
- Request routing to dummy internal services
- Request/violation logging to MongoDB
- Simple monitoring endpoint

## Architecture

- `GatewayFilter` (`OncePerRequestFilter`)
  - Validates JWT for protected routes
  - Applies token bucket rate limiting
  - Records request logs and rate counter snapshots
- `GatewayController`
  - Centralized gateway entry point (`/gateway/**`)
  - Routes to internal service handlers
- `DummyInternalService`
  - Simulated backend/internal service responses
- `ApiLogService`
  - Persists request logs and minute-level counters in MongoDB

## Endpoints

- `POST /auth/login` - get JWT token
- `GET /gateway/service-a/hello` - protected route via gateway
- `GET /gateway/service-b/hello` - protected route via gateway
- `GET /monitor/logs?limit=20` - protected monitoring route
- `GET /internal/service-a/hello` - internal dummy endpoint (unprotected)
- `GET /internal/service-b/hello` - internal dummy endpoint (unprotected)

## Default Credentials

- `alice / alice123`
- `bob / bob123`
- `admin / admin123`

## Configuration

`src/main/resources/application.yml`:

- `gateway.rate-limit.requests-per-minute` (default: `100`)
- `gateway.rate-limit.burst-capacity` (default: `100`)
- `gateway.auth.jwt-secret`
- `gateway.auth.jwt-expiration-seconds`
- `spring.data.mongodb.uri`

You can override with environment variables:
- `MONGO_URI`
- `JWT_SECRET`
- `JWT_EXPIRATION_SECONDS`
- `RATE_LIMIT_PER_MINUTE`
- `RATE_LIMIT_BURST_CAPACITY`

## Run

1. Start MongoDB locally (or provide `MONGO_URI`).
2. Build and run:

```bash
mvn spring-boot:run
```

## Quick Try

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"alice","password":"alice123"}' | jq -r '.token')

curl -s http://localhost:8080/gateway/service-a/hello -H "Authorization: Bearer $TOKEN"
curl -s http://localhost:8080/monitor/logs?limit=5 -H "Authorization: Bearer $TOKEN"
```

## Rate Limiter Design Notes

### Why Token Bucket
- Supports bursts while preserving long-term rate limits.
- Better user experience than strict fixed windows at boundary times.

### Scaling Approach
- Current implementation keeps bucket state in-memory (`ConcurrentHashMap`).
- For distributed deployment, move token state to Redis (atomic LUA/script updates) so all nodes share limits.

### High Traffic Behavior
- In-memory token checks are O(1) and lock only per user bucket.
- Mongo logging write amplification can become a bottleneck; use async queue/batch writes in production.

### Data Consistency vs Performance
- Request allow/deny path prioritizes speed and availability.
- Monitoring counters are eventual-consistency friendly; exact log ordering is less critical than throughput.

### Distributed System Considerations
- Shared state store (Redis) for rate limiting.
- Stateless JWT auth works across replicas.
- Centralized log pipeline (Kafka -> Mongo/Elastic) for high-volume observability.

## Tests

```bash
mvn test
```

