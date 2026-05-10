# Campus Bus Backend

This workspace now contains the microservice version of the Campus Bus backend.

The old monolith source code has been removed from the root `src/` folder to avoid confusion. The active backend code is under `services/`.

## Active Services

| Service | Port | Database |
| --- | ---: | --- |
| `gateway-service` | 8080 | None |
| `auth-service` | 8081 | `auth_db` |
| `route-service` | 8082 | `route_db` |
| `stop-service` | 8083 | `stop_db` |
| `bus-service` | 8084 | `bus_db` |
| `trip-service` | 8085 | `trip_db` |
| `location-service` | 8086 | `location_db` |

## Build Everything

```powershell
./mvnw test
```

or:

```powershell
./mvnw -f services/pom.xml test
```

## Run With Docker Compose

```powershell
docker compose up --build
```

This starts 7 Spring Boot service containers and 6 PostgreSQL containers. The frontend should use the gateway at `http://localhost:8080`; direct service ports remain useful for Swagger and debugging.

| Service | App URL | PostgreSQL |
| --- | --- | --- |
| `gateway-service` | `http://localhost:8080` | None |
| `auth-service` | `http://localhost:8081/swagger-ui.html` | `localhost:5433/auth_db` |
| `route-service` | `http://localhost:8082/swagger-ui.html` | `localhost:5434/route_db` |
| `stop-service` | `http://localhost:8083/swagger-ui.html` | `localhost:5435/stop_db` |
| `bus-service` | `http://localhost:8084/swagger-ui.html` | `localhost:5436/bus_db` |
| `trip-service` | `http://localhost:8085/swagger-ui.html` | `localhost:5437/trip_db` |
| `location-service` | `http://localhost:8086/swagger-ui.html` | `localhost:5438/location_db` |

Stop everything:

```powershell
docker compose down
```

## Basic Frontend Demo

Use this simple static page to test backend business flows from a browser.

```powershell
cd frontend-smoke-test
python -m http.server 4200
```

Open:

```text
http://localhost:4200
```

The page calls the gateway on `http://localhost:8080`. It can login as admin/driver, load public route and live bus data, create routes/stops/buses, start/complete trips, send GPS locations, and receive WebSocket live location updates.

## Demo SQL Seeds

The backend auto-seeds demo data on startup. SQL seed files are also stored in `database/seed/` for GitHub review or manual DataGrip/psql import.

See `database/README.md` for the database-to-file mapping and import commands.

## Run A Service

Example:

```powershell
./mvnw -f services/auth-service/pom.xml spring-boot:run
```

Swagger:

```text
http://localhost:8081/swagger-ui.html
```

## Documentation

- Microservice guide: `services/README.md`
- Detailed assignment reports and walkthrough notes are kept locally in `docs/` and intentionally excluded from GitHub.
