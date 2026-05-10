const services = {
  auth: "http://localhost:8080",
  route: "http://localhost:8080",
  stop: "http://localhost:8080",
  bus: "http://localhost:8080",
  trip: "http://localhost:8080",
  location: "http://localhost:8080",
};

const accounts = {
  ADMIN: { email: "admin@campusbus.com", password: "Admin123!" },
  DRIVER: { email: "driver@campusbus.com", password: "Driver123!" },
};

const state = {
  token: "",
  role: "STUDENT",
  user: null,
  liveLocations: [],
  map: null,
  stopLayer: null,
  busLayer: null,
  liveSocket: null,
  liveSocketReconnectTimer: null,
};

const els = {
  statusDot: document.querySelector("#statusDot"),
  statusText: document.querySelector("#statusText"),
  statusDetail: document.querySelector("#statusDetail"),
  logoutBtn: document.querySelector("#logoutBtn"),
  adminAccessPill: document.querySelector("#adminAccessPill"),
  driverAccessPill: document.querySelector("#driverAccessPill"),
  routesList: document.querySelector("#routesList"),
  stopsList: document.querySelector("#stopsList"),
  liveBusList: document.querySelector("#liveBusList"),
  adminDataList: document.querySelector("#adminDataList"),
  driverDataList: document.querySelector("#driverDataList"),
  logBox: document.querySelector("#logBox"),
  webSocketStatus: document.querySelector("#webSocketStatus"),
};

const campusCenter = [2.946, 101.876];
const liveSocketUrl = "ws://localhost:8080/ws/locations/live";
const busIcon = typeof L === "undefined" ? null : L.divIcon({
  className: "bus-map-marker",
  html: "<span>BUS</span>",
  iconSize: [42, 42],
  iconAnchor: [21, 21],
  popupAnchor: [0, -18],
});

function stamp() {
  return new Date().toISOString().replace(/[-:.TZ]/g, "").slice(0, 14);
}

function log(title, payload, type = "info") {
  const time = new Date().toLocaleTimeString();
  const label = type === "error" ? "FAIL" : type === "ok" ? "OK" : "INFO";
  const body = typeof payload === "string" ? payload : JSON.stringify(payload, null, 2);
  els.logBox.textContent = `[${time}] ${label} ${title}\n${body}\n\n${els.logBox.textContent}`;
}

function setStatus(text, detail, mode = "idle") {
  els.statusText.textContent = text;
  els.statusDetail.textContent = detail;
  els.statusDot.className = `status-dot ${mode}`;
}

function setWebSocketStatus(text, mode = "idle") {
  els.webSocketStatus.textContent = text;
  els.webSocketStatus.className = `ws-status ${mode}`;
}

function setRole(role, user = null, token = "") {
  state.role = role;
  state.user = user;
  state.token = token;

  if (role === "ADMIN") {
    setStatus("Logged in as Admin", user?.email || "ADMIN", "ok");
  } else if (role === "DRIVER") {
    setStatus("Logged in as Driver", user?.email || "DRIVER", "ok");
  } else {
    setStatus("Student View", "Public APIs only. No login required.", "idle");
  }

  els.logoutBtn.classList.toggle("hidden", role === "STUDENT");
  els.adminAccessPill.textContent = role === "ADMIN" ? "Admin access active" : "Login as Admin to use write APIs";
  els.driverAccessPill.textContent = role === "DRIVER" ? "Driver access active" : "Login as Driver to use trip APIs";
  els.adminAccessPill.classList.toggle("ok", role === "ADMIN");
  els.driverAccessPill.classList.toggle("ok", role === "DRIVER");
}

function initMap() {
  if (state.map) {
    return;
  }
  if (typeof L === "undefined") {
    log("Map unavailable", "Leaflet did not load. Check your internet connection or CDN access.", "error");
    return;
  }

  state.map = L.map("campusMap", {
    center: campusCenter,
    zoom: 16,
    scrollWheelZoom: true,
  });

  L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
    maxZoom: 19,
    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
  }).addTo(state.map);

  state.stopLayer = L.layerGroup().addTo(state.map);
  state.busLayer = L.layerGroup().addTo(state.map);
}

function markerBounds(points) {
  if (typeof L === "undefined") {
    return null;
  }
  const validPoints = points.filter((point) => Number.isFinite(point[0]) && Number.isFinite(point[1]));
  if (!validPoints.length) {
    return null;
  }
  return L.latLngBounds(validPoints);
}

function fitMap(points) {
  initMap();
  if (!state.map || typeof L === "undefined") {
    return;
  }
  const bounds = markerBounds(points);
  if (bounds) {
    state.map.fitBounds(bounds.pad(0.25), { maxZoom: 17 });
  }
}

function renderStopMarkers(stops) {
  initMap();
  if (!state.stopLayer || typeof L === "undefined") {
    return;
  }
  state.stopLayer.clearLayers();

  const points = stops.map((stop) => [Number(stop.latitude), Number(stop.longitude)]);
  stops.forEach((stop) => {
    L.circleMarker([Number(stop.latitude), Number(stop.longitude)], {
      radius: 8,
      color: "#116045",
      weight: 2,
      fillColor: "#1e8d63",
      fillOpacity: 0.88,
    })
      .bindPopup(`<strong>${stop.sequenceNo}. ${stop.stopName}</strong><br>Route ${stop.routeId}<br>${stop.latitude}, ${stop.longitude}`)
      .addTo(state.stopLayer);
  });

  fitMap(points);
}

function renderBusMarkers(locations) {
  initMap();
  if (!state.busLayer || typeof L === "undefined") {
    return;
  }
  state.busLayer.clearLayers();

  const points = locations.map((location) => [Number(location.latitude), Number(location.longitude)]);
  locations.forEach((location) => {
    const marker = L.marker([Number(location.latitude), Number(location.longitude)], {
      title: `Bus ${location.busId}`,
      icon: busIcon,
    });
    marker.bindPopup(`<strong>Bus ${location.busId}</strong><br>Trip ${location.tripId}<br>${location.latitude}, ${location.longitude}<br>${location.recordedAt}`);
    marker.addTo(state.busLayer);
  });

  fitMap(points);
}

function requireRole(role) {
  if (state.role !== role || !state.token) {
    throw new Error(`Please login as ${role} first.`);
  }
}

function getFormData(form) {
  return Object.fromEntries(new FormData(form).entries());
}

function numberValue(value) {
  return Number(value);
}

async function api(label, service, path, options = {}) {
  const headers = {
    "Content-Type": "application/json",
    ...(options.auth ? { Authorization: `Bearer ${state.token}` } : {}),
  };

  const response = await fetch(`${services[service]}${path}`, {
    method: options.method || "GET",
    headers,
    body: options.body ? JSON.stringify(options.body) : undefined,
  });

  const text = await response.text();
  let payload = null;
  try {
    payload = text ? JSON.parse(text) : null;
  } catch {
    payload = text;
  }

  if (!response.ok) {
    const message = payload?.message || payload?.error || `HTTP ${response.status}`;
    const error = new Error(message);
    error.payload = payload;
    error.status = response.status;
    throw error;
  }

  log(label, payload, "ok");
  return payload;
}

async function action(label, fn) {
  try {
    setStatus("Working...", label, "busy");
    const result = await fn();
    if (state.role === "STUDENT") {
      setStatus("Student View", "Last action succeeded.", "ok");
    } else {
      setStatus(`Logged in as ${state.role[0]}${state.role.slice(1).toLowerCase()}`, "Last action succeeded.", "ok");
    }
    return result;
  } catch (error) {
    setStatus("Action failed", error.message, "fail");
    log(label, error.payload || error.message, "error");
  }
}

function card(title, rows = []) {
  const detailRows = rows
    .filter(([, value]) => value !== undefined && value !== null)
    .map(([key, value]) => `<p><span>${key}</span><strong>${value}</strong></p>`)
    .join("");
  return `<article class="data-card"><h4>${title}</h4>${detailRows}</article>`;
}

function renderCards(target, html, emptyText) {
  target.classList.toggle("empty", !html);
  target.innerHTML = html || emptyText;
}

function renderRoutes(target, routes) {
  const html = routes.map((route) => card(route.routeName, [
    ["ID", route.id],
    ["Description", route.description],
  ])).join("");
  renderCards(target, html, "No routes found.");
}

function renderStops(stops) {
  const html = stops.map((stop) => card(`${stop.sequenceNo}. ${stop.stopName}`, [
    ["ID", stop.id],
    ["Route", stop.routeId],
    ["Latitude", stop.latitude],
    ["Longitude", stop.longitude],
  ])).join("");
  renderCards(els.stopsList, html, "No stops found.");
  renderStopMarkers(stops);
}

function renderBuses(target, buses) {
  const html = buses.map((bus) => card(bus.busCode, [
    ["ID", bus.id],
    ["Plate", bus.plateNumber],
    ["Route", bus.routeId],
  ])).join("");
  renderCards(target, html, "No buses found.");
}

function renderLiveBuses(target, locations) {
  state.liveLocations = locations;
  const html = locations.map((location) => card(`Bus ${location.busId}`, [
    ["Trip", location.tripId],
    ["Latitude", location.latitude],
    ["Longitude", location.longitude],
    ["Recorded", location.recordedAt],
  ])).join("");
  renderCards(target, html, "No live bus locations found.");
  renderBusMarkers(locations);
}

function mergeLiveLocation(location) {
  const nextLocations = state.liveLocations.filter((item) => item.tripId !== location.tripId);
  nextLocations.unshift(location);
  renderLiveBuses(els.liveBusList, nextLocations);
  log("WebSocket live location", location, "ok");
}

function connectLiveSocket() {
  if (state.liveSocket && [WebSocket.CONNECTING, WebSocket.OPEN].includes(state.liveSocket.readyState)) {
    return;
  }

  setWebSocketStatus("Connecting...", "busy");
  state.liveSocket = new WebSocket(liveSocketUrl);

  state.liveSocket.addEventListener("open", () => {
    setWebSocketStatus("Connected", "ok");
    log("WebSocket connected", liveSocketUrl, "ok");
  });

  state.liveSocket.addEventListener("message", (event) => {
    try {
      const payload = JSON.parse(event.data);
      if (payload?.data) {
        mergeLiveLocation(payload.data);
      }
    } catch (error) {
      log("WebSocket parse failed", event.data, "error");
    }
  });

  state.liveSocket.addEventListener("close", () => {
    setWebSocketStatus("Disconnected. Reconnecting...", "fail");
    window.clearTimeout(state.liveSocketReconnectTimer);
    state.liveSocketReconnectTimer = window.setTimeout(connectLiveSocket, 3000);
  });

  state.liveSocket.addEventListener("error", () => {
    setWebSocketStatus("Connection error", "fail");
  });
}

function renderCreated(target, title, data) {
  const html = card(title, Object.entries(data || {}).map(([key, value]) => [key, value]));
  renderCards(target, html, "No data yet.");
}

async function login(role) {
  const payload = await api(`${role} login`, "auth", "/api/auth/login", {
    method: "POST",
    body: accounts[role],
  });
  const data = payload.data;
  setRole(data.role, data, data.accessToken);
  log("Current session", { role: data.role, email: data.email, userId: data.userId }, "ok");
}

async function loadRoutes(target = els.routesList) {
  const payload = await api("Load routes", "route", "/api/routes");
  renderRoutes(target, payload.data || []);
  return payload.data || [];
}

async function loadStops(routeId) {
  const payload = await api(`Load stops for route ${routeId}`, "stop", `/api/routes/${routeId}/stops`);
  renderStops(payload.data || []);
}

async function loadLive(target = els.liveBusList) {
  const payload = await api("Load live buses", "location", "/api/buses/live");
  renderLiveBuses(target, payload.data || []);
}

async function loadBuses() {
  requireRole("ADMIN");
  const payload = await api("Load buses", "bus", "/api/buses", { auth: true });
  renderBuses(els.adminDataList, payload.data || []);
}

async function createRoute(form) {
  requireRole("ADMIN");
  const data = getFormData(form);
  const payload = await api("Create route", "route", "/api/routes", {
    method: "POST",
    auth: true,
    body: {
      routeName: `${data.routeName} ${stamp()}`,
      description: data.description,
    },
  });
  renderCreated(els.adminDataList, "Created Route", payload.data);
  form.routeName.value = "Frontend Route";
  await loadRoutes(els.routesList);
}

async function createStop(form) {
  requireRole("ADMIN");
  const data = getFormData(form);
  const payload = await api("Create stop", "stop", "/api/stops", {
    method: "POST",
    auth: true,
    body: {
      routeId: numberValue(data.routeId),
      stopName: `${data.stopName} ${stamp()}`,
      latitude: numberValue(data.latitude),
      longitude: numberValue(data.longitude),
      sequenceNo: numberValue(data.sequenceNo),
    },
  });
  renderCreated(els.adminDataList, "Created Stop", payload.data);
}

async function createBus(form) {
  requireRole("ADMIN");
  const data = getFormData(form);
  const suffix = stamp();
  const payload = await api("Create bus", "bus", "/api/buses", {
    method: "POST",
    auth: true,
    body: {
      busCode: `${data.busCode}-${suffix}`,
      plateNumber: `${data.plateNumber}-${suffix.slice(-4)}`,
      routeId: numberValue(data.routeId),
    },
  });
  renderCreated(els.adminDataList, "Created Bus", payload.data);
}

async function startTrip(form) {
  requireRole("DRIVER");
  const data = getFormData(form);
  const payload = await api("Start trip", "trip", "/api/trips/start", {
    method: "POST",
    auth: true,
    body: { busId: numberValue(data.busId) },
  });
  renderCreated(els.driverDataList, "Started Trip", payload.data);
  document.querySelector("#sendLocationForm [name='tripId']").value = payload.data.id;
  document.querySelector("#sendLocationForm [name='busId']").value = payload.data.busId;
  document.querySelector("#completeTripForm [name='tripId']").value = payload.data.id;
}

async function sendLocation(form) {
  requireRole("DRIVER");
  const data = getFormData(form);
  const payload = await api("Send location", "location", "/api/locations", {
    method: "POST",
    auth: true,
    body: {
      tripId: numberValue(data.tripId),
      busId: numberValue(data.busId),
      latitude: numberValue(data.latitude),
      longitude: numberValue(data.longitude),
    },
  });
  renderCreated(els.driverDataList, "Saved Location", payload.data);
}

async function completeTrip(form) {
  requireRole("DRIVER");
  const data = getFormData(form);
  const payload = await api("Complete trip", "trip", `/api/trips/${data.tripId}/complete`, {
    method: "POST",
    auth: true,
  });
  renderCreated(els.driverDataList, "Completed Trip", payload.data);
}

function bindForm(selector, label, handler) {
  document.querySelector(selector).addEventListener("submit", (event) => {
    event.preventDefault();
    action(label, () => handler(event.currentTarget));
  });
}

function bindClick(selector, label, handler) {
  document.querySelector(selector).addEventListener("click", () => action(label, handler));
}

document.querySelectorAll("[data-login-role]").forEach((button) => {
  button.addEventListener("click", () => action(`Login ${button.dataset.loginRole}`, () => login(button.dataset.loginRole)));
});

document.querySelectorAll("[data-tab]").forEach((button) => {
  button.addEventListener("click", () => {
    document.querySelectorAll("[data-tab]").forEach((tab) => tab.classList.remove("active"));
    document.querySelectorAll(".tab-panel").forEach((panel) => panel.classList.remove("active"));
    button.classList.add("active");
    document.querySelector(`#${button.dataset.tab}`).classList.add("active");
    if (button.dataset.tab === "student") {
      initMap();
      setTimeout(() => state.map?.invalidateSize(), 0);
    }
  });
});

els.logoutBtn.addEventListener("click", () => {
  setRole("STUDENT");
  log("Logout", "Session cleared.");
});

bindClick("#healthBtn", "Check connection", async () => {
  await loadRoutes(els.routesList);
  await loadLive(els.liveBusList);
});
bindClick("#loadRoutesBtn", "Load routes", () => loadRoutes(els.routesList));
bindClick("#loadLiveBtn", "Load live buses", () => loadLive(els.liveBusList));
bindClick("#adminLoadRoutesBtn", "Admin load routes", () => loadRoutes(els.adminDataList));
bindClick("#adminLoadBusesBtn", "Admin load buses", loadBuses);
bindClick("#driverLoadLiveBtn", "Driver refresh live buses", () => loadLive(els.driverDataList));

bindForm("#studentStopsForm", "Load stops", (form) => loadStops(getFormData(form).routeId));
bindForm("#createRouteForm", "Create route", createRoute);
bindForm("#createStopForm", "Create stop", createStop);
bindForm("#createBusForm", "Create bus", createBus);
bindForm("#startTripForm", "Start trip", startTrip);
bindForm("#sendLocationForm", "Send location", sendLocation);
bindForm("#completeTripForm", "Complete trip", completeTrip);

document.querySelector("#clearLogBtn").addEventListener("click", () => {
  els.logBox.textContent = "";
});

setRole("STUDENT");
initMap();
connectLiveSocket();
log("Frontend ready", "Run docker compose up --build, then serve this folder on http://localhost:4200.");
