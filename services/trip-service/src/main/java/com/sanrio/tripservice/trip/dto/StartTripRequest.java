package com.sanrio.tripservice.trip.dto;

import jakarta.validation.constraints.NotNull;

public record StartTripRequest(@NotNull Long busId) {
}
