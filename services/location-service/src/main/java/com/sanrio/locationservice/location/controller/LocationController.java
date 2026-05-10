package com.sanrio.locationservice.location.controller;

import com.sanrio.locationservice.common.ApiResponse;
import com.sanrio.locationservice.security.JwtPrincipal;
import com.sanrio.locationservice.location.dto.CreateLocationRequest;
import com.sanrio.locationservice.location.dto.LiveBusResponse;
import com.sanrio.locationservice.location.dto.LocationResponse;
import com.sanrio.locationservice.location.service.LocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LocationController {
    private final LocationService locationService;

    @PostMapping("/locations")
    public ResponseEntity<ApiResponse<LocationResponse>> createLocation(Authentication authentication, @Valid @RequestBody CreateLocationRequest request) {
        JwtPrincipal principal = (JwtPrincipal) authentication.getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>("Location recorded successfully", locationService.saveLocation(principal.userId(), request)));
    }

    @GetMapping("/buses/live")
    public ResponseEntity<ApiResponse<List<LiveBusResponse>>> getLiveBuses() {
        return ResponseEntity.ok(new ApiResponse<>("Live bus locations retrieved successfully", locationService.getLiveBuses()));
    }
}
