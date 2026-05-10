package com.sanrio.tripservice.trip.service;

import com.sanrio.tripservice.common.BadRequestException;
import com.sanrio.tripservice.common.ResourceNotFoundException;
import com.sanrio.tripservice.trip.dto.StartTripRequest;
import com.sanrio.tripservice.trip.dto.TripResponse;
import com.sanrio.tripservice.trip.entity.Trip;
import com.sanrio.tripservice.trip.entity.TripStatus;
import com.sanrio.tripservice.trip.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TripService {
    private final TripRepository tripRepository;

    @Transactional
    public TripResponse startTrip(Long driverId, StartTripRequest request) {
        if (tripRepository.existsByBusIdAndStatus(request.busId(), TripStatus.ACTIVE)) {
            throw new BadRequestException("This bus already has an active trip");
        }
        if (tripRepository.existsByDriverIdAndStatus(driverId, TripStatus.ACTIVE)) {
            throw new BadRequestException("This driver already has an active trip");
        }
        Trip trip = tripRepository.save(Trip.builder()
                .busId(request.busId())
                .driverId(driverId)
                .startTime(Instant.now())
                .status(TripStatus.ACTIVE)
                .build());
        return toResponse(trip);
    }

    @Transactional
    public TripResponse completeTrip(Long driverId, Long tripId) {
        Trip trip = tripRepository.findByIdAndDriverId(tripId, driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Active trip not found: " + tripId));
        if (trip.getStatus() == TripStatus.COMPLETED) {
            throw new BadRequestException("This trip is already completed");
        }
        trip.setStatus(TripStatus.COMPLETED);
        trip.setEndTime(Instant.now());
        return toResponse(trip);
    }

    private TripResponse toResponse(Trip trip) {
        return new TripResponse(trip.getId(), trip.getBusId(), trip.getDriverId(), trip.getStartTime(), trip.getEndTime(), trip.getStatus().name());
    }
}
