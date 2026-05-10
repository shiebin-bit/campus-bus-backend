package com.sanrio.tripservice.trip.controller;

import com.sanrio.tripservice.common.ApiResponse;
import com.sanrio.tripservice.security.JwtPrincipal;
import com.sanrio.tripservice.trip.dto.StartTripRequest;
import com.sanrio.tripservice.trip.dto.TripResponse;
import com.sanrio.tripservice.trip.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {
    private final TripService tripService;

    @PostMapping("/start")
    public ResponseEntity<ApiResponse<TripResponse>> startTrip(Authentication authentication, @Valid @RequestBody StartTripRequest request) {
        JwtPrincipal principal = (JwtPrincipal) authentication.getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>("Trip started successfully", tripService.startTrip(principal.userId(), request)));
    }

    @PostMapping("/{tripId}/complete")
    public ResponseEntity<ApiResponse<TripResponse>> completeTrip(Authentication authentication, @PathVariable Long tripId) {
        JwtPrincipal principal = (JwtPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(new ApiResponse<>("Trip completed successfully", tripService.completeTrip(principal.userId(), tripId)));
    }
}
