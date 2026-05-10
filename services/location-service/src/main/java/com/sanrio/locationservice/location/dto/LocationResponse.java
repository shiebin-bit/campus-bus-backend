package com.sanrio.locationservice.location.dto;

import java.time.Instant;

public record LocationResponse(Long id, Long tripId, Long busId, Long driverId, Double latitude, Double longitude, Instant recordedAt) {
}
