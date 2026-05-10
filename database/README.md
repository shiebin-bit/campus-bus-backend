# Campus Bus Demo SQL Seeds

This folder stores the demo database seed files used by the microservice backend.

Each Spring Boot service owns its own PostgreSQL container and database, so the seed data is split by database:

| Service | Container | Host Port | Database | SQL File |
| --- | --- | ---: | --- | --- |
| auth-service | auth-db | 5433 | auth_db | seed/auth_db.sql |
| route-service | route-db | 5434 | route_db | seed/route_db.sql |
| stop-service | stop-db | 5435 | stop_db | seed/stop_db.sql |
| bus-service | bus-db | 5436 | bus_db | seed/bus_db.sql |
| trip-service | trip-db | 5437 | trip_db | seed/trip_db.sql |
| location-service | location-db | 5438 | location_db | seed/location_db.sql |

The application also seeds the same demo data automatically on startup through each service's `DataSeeder`.

## Import Example

Run these from the project root if you want to manually reseed a running Docker database:

```powershell
Get-Content database/seed/auth_db.sql | docker exec -i auth-db psql -U postgres -d auth_db
Get-Content database/seed/route_db.sql | docker exec -i route-db psql -U postgres -d route_db
Get-Content database/seed/stop_db.sql | docker exec -i stop-db psql -U postgres -d stop_db
Get-Content database/seed/bus_db.sql | docker exec -i bus-db psql -U postgres -d bus_db
Get-Content database/seed/trip_db.sql | docker exec -i trip-db psql -U postgres -d trip_db
Get-Content database/seed/location_db.sql | docker exec -i location-db psql -U postgres -d location_db
```

Demo accounts:

- Admin: `admin@campusbus.com / Admin123!`
- Driver: `driver@campusbus.com / Driver123!`
- Extra drivers use the same demo password: `Driver123!`
