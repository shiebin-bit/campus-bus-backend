package com.sanrio.busservice.bus.controller;

import com.sanrio.busservice.bus.dto.BusResponse;
import com.sanrio.busservice.bus.dto.CreateBusRequest;
import com.sanrio.busservice.bus.dto.UpdateBusRequest;
import com.sanrio.busservice.bus.service.BusService;
import com.sanrio.busservice.common.ApiResponse;
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
@RequestMapping("/api/buses")
@RequiredArgsConstructor
public class BusController {
    private final BusService busService;

    @PostMapping
    public ResponseEntity<ApiResponse<BusResponse>> createBus(@Valid @RequestBody CreateBusRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>("Bus created successfully", busService.createBus(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BusResponse>>> getBuses() {
        return ResponseEntity.ok(new ApiResponse<>("Buses retrieved successfully", busService.getBuses()));
    }

    @GetMapping("/{busId}")
    public ResponseEntity<ApiResponse<BusResponse>> getBus(@PathVariable Long busId) {
        return ResponseEntity.ok(new ApiResponse<>("Bus retrieved successfully", busService.getBus(busId)));
    }

    @PutMapping("/{busId}")
    public ResponseEntity<ApiResponse<BusResponse>> updateBus(@PathVariable Long busId, @Valid @RequestBody UpdateBusRequest request) {
        return ResponseEntity.ok(new ApiResponse<>("Bus updated successfully", busService.updateBus(busId, request)));
    }

    @DeleteMapping("/{busId}")
    public ResponseEntity<Void> deleteBus(@PathVariable Long busId) {
        busService.deleteBus(busId);
        return ResponseEntity.noContent().build();
    }
}
