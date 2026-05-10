package com.sanrio.busservice.bus.dto;

public record BusResponse(Long id, String busCode, String plateNumber, Long routeId) {
}
