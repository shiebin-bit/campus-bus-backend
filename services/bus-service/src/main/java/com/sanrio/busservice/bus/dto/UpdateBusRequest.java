package com.sanrio.busservice.bus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateBusRequest(@NotBlank String busCode, @NotBlank String plateNumber, @NotNull Long routeId) {
}
