# Campus Bus Basic Frontend Demo

This is a simple static frontend for manually testing the Campus Bus backend business flows.

It is not a full Angular/Ionic application yet. It is a lightweight demo page that proves the backend APIs can be used from a browser.

The page tries the API Gateway first:

```text
http://localhost:8080
```

If the gateway is not reachable, the page automatically falls back to the individual service ports `8081-8086` so the demo can still verify auth, admin, driver, and student flows.

## Run

Start the backend first:

```powershell
docker compose up --build
```

Then serve this folder on port `4200`:

```powershell
cd frontend-smoke-test
python -m http.server 4200
```

Open:

```text
http://localhost:4200
```

Port `4200` is used because the gateway CORS config already allows Angular-style local frontend origins.
VS Code Live Server on `http://localhost:5500` is also allowed, but restart the backend with `docker compose up --build` after CORS changes.

## What You Can Test

- Student/public view: routes, route stops, and live buses.
- OpenStreetMap view: route stops and live buses appear as map markers.
- Admin role: create routes, stops, and buses.
- Driver role: start trips, send GPS locations, and complete trips.
- WebSocket live updates: driver GPS submissions update the student map/list without manual refresh.
- Request log: see API responses and errors while testing.

The full presentation script is kept locally under `docs/` and is intentionally excluded from GitHub.

## Map Notes

- The map uses OpenStreetMap through Leaflet CDN.
- No Google Maps API key is required.
- Internet access is needed for the CDN and map tiles.
- If Leaflet cannot load, the list-based frontend still works and the request log will show a map warning.

## Demo Accounts

- Admin: `admin@campusbus.com / Admin123!`
- Driver: `driver@campusbus.com / Driver123!`
