package com.sanrio.routeservice.route.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateRouteRequest(@NotBlank String routeName, @NotBlank String description) {
}
