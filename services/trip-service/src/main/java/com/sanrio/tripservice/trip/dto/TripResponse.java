package com.sanrio.tripservice.trip.dto;

import java.time.Instant;

public record TripResponse(Long id, Long busId, Long driverId, Instant startTime, Instant endTime, String status) {
}
