# Campus Bus Microservices

This folder contains the active microservice version of the Campus Bus backend.

The old monolith source code has been removed from the root `src/` folder to avoid confusion. The archived monolith learning notes are under `docs/archive/`.

## Services

| Service | Port | Database | Responsibility |
| --- | ---: | --- | --- |
| `auth-service` | 8081 | `auth_db` | Login, users, JWT issuing |
| `route-service` | 8082 | `route_db` | Routes |
| `stop-service` | 8083 | `stop_db` | Stops by route id |
| `bus-service` | 8084 | `bus_db` | Buses by route id |
| `trip-service` | 8085 | `trip_db` | Driver starts trips |
| `location-service` | 8086 | `location_db` | GPS updates and live bus lookup |

## Internal Package Layers

Each service now keeps its business code separated by layer:

- `controller` handles HTTP endpoints.
- `service` handles business rules.
- `repository` handles database access.
- `entity` maps database tables.
- `dto` defines request and response bodies.

Shared technical packages such as `common` and `security` stay outside those business layers.

## Important Microservice Difference

In the monolith, JPA entities can reference each other directly, for example `Bus -> Route`.

In microservices, each service owns its own database. Because of that, cross-service relationships are stored as IDs only:

- `stop-service` stores `routeId`, not a `Route` entity.
- `bus-service` stores `routeId`, not a `Route` entity.
- `trip-service` stores `busId` and `driverId`, not `Bus` and `User` entities.
- `location-service` stores `tripId`, `busId`, and `driverId`, not `Trip` and `Bus` entities.

This is the correct first step for separating databases.

For a more detailed learning walkthrough, read `docs/archive/MICROSERVICE_CODE_WALKTHROUGH.md` from the project root.

## JWT Responsibility

- `auth-service` issues JWT tokens.
- Other services validate JWT tokens using the same shared secret for now.
- Later, a better production design would use asymmetric keys: auth-service signs with a private key, other services verify with a public key.

## Docker Runtime

The project now uses a 12-container Docker Compose setup:

- 6 Spring Boot service containers.
- 6 PostgreSQL containers.
- 6 separate PostgreSQL volumes.
- Host ports `5433-5438` are exposed for DataGrip.

Start everything from the project root:

```powershell
docker compose up --build
```

Stop everything:

```powershell
docker compose down
```

DataGrip connections use host `localhost`, user `postgres`, password `123456`.

| Database | Host port |
| --- | ---: |
| `auth_db` | 5433 |
| `route_db` | 5434 |
| `stop_db` | 5435 |
| `bus_db` | 5436 |
| `trip_db` | 5437 |
| `location_db` | 5438 |

## Build All Services

From the project root:

```powershell
mvn -f services/pom.xml test
```

## Run One Service

Example:

```powershell
mvn -f services/auth-service/pom.xml spring-boot:run
```

Swagger URLs:

- Auth: http://localhost:8081/swagger-ui.html
- Route: http://localhost:8082/swagger-ui.html
- Stop: http://localhost:8083/swagger-ui.html
- Bus: http://localhost:8084/swagger-ui.html
- Trip: http://localhost:8085/swagger-ui.html
- Location: http://localhost:8086/swagger-ui.html

## Current Limitations

This split is a first microservice scaffold, not a full production system yet.

Still missing for a complete production-grade microservice architecture:

- API gateway
- service discovery
- inter-service HTTP clients
- centralized config
- distributed transaction strategy
- route/bus/trip enrichment across services

For the assignment and learning stage, this is a safe first split.
