package com.sanrio.stopservice.stop.controller;

import com.sanrio.stopservice.common.ApiResponse;
import com.sanrio.stopservice.stop.dto.CreateStopRequest;
import com.sanrio.stopservice.stop.dto.StopResponse;
import com.sanrio.stopservice.stop.dto.UpdateStopRequest;
import com.sanrio.stopservice.stop.service.StopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StopController {
    private final StopService stopService;

    @PostMapping("/stops")
    public ResponseEntity<ApiResponse<StopResponse>> createStop(@Valid @RequestBody CreateStopRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>("Stop created successfully", stopService.createStop(request)));
    }

    @GetMapping("/routes/{routeId}/stops")
    public ResponseEntity<ApiResponse<List<StopResponse>>> getStopsByRoute(@PathVariable Long routeId) {
        return ResponseEntity.ok(new ApiResponse<>("Stops retrieved successfully", stopService.getStopsByRoute(routeId)));
    }

    @GetMapping("/stops")
    public ResponseEntity<ApiResponse<List<StopResponse>>> getStops() {
        return ResponseEntity.ok(new ApiResponse<>("Stops retrieved successfully", stopService.getStops()));
    }

    @PutMapping("/stops/{stopId}")
    public ResponseEntity<ApiResponse<StopResponse>> updateStop(@PathVariable Long stopId, @Valid @RequestBody UpdateStopRequest request) {
        return ResponseEntity.ok(new ApiResponse<>("Stop updated successfully", stopService.updateStop(stopId, request)));
    }

    @DeleteMapping("/stops/{stopId}")
    public ResponseEntity<Void> deleteStop(@PathVariable Long stopId) {
        stopService.deleteStop(stopId);
        return ResponseEntity.noContent().build();
    }
}
