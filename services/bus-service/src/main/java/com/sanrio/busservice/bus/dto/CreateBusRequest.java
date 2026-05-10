package com.sanrio.busservice.bus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateBusRequest(@NotBlank String busCode, @NotBlank String plateNumber, @NotNull Long routeId) {
}
