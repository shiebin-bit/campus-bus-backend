package com.sanrio.tripservice.trip.service;

import com.sanrio.tripservice.common.BadRequestException;
import com.sanrio.tripservice.common.ResourceNotFoundException;
import com.sanrio.tripservice.trip.dto.StartTripRequest;
import com.sanrio.tripservice.trip.dto.TripResponse;
import com.sanrio.tripservice.trip.entity.Trip;
import com.sanrio.tripservice.trip.entity.TripStatus;
import com.sanrio.tripservice.trip.repository.TripRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TripServiceTest {
    @Mock
    private TripRepository tripRepository;

    @InjectMocks
    private TripService tripService;

    @Test
    void startTripCreatesActiveTripWhenBusAndDriverAreFree() {
        StartTripRequest request = new StartTripRequest(201L);
        when(tripRepository.existsByBusIdAndStatus(201L, TripStatus.ACTIVE)).thenReturn(false);
        when(tripRepository.existsByDriverIdAndStatus(2L, TripStatus.ACTIVE)).thenReturn(false);
        when(tripRepository.save(any(Trip.class))).thenAnswer(invocation -> {
            Trip trip = invocation.getArgument(0);
            trip.setId(301L);
            return trip;
        });

        TripResponse response = tripService.startTrip(2L, request);

        assertThat(response.id()).isEqualTo(301L);
        assertThat(response.busId()).isEqualTo(201L);
        assertThat(response.driverId()).isEqualTo(2L);
        assertThat(response.status()).isEqualTo("ACTIVE");
        assertThat(response.startTime()).isNotNull();
        verify(tripRepository).save(any(Trip.class));
    }

    @Test
    void startTripRejectsBusWithActiveTrip() {
        when(tripRepository.existsByBusIdAndStatus(201L, TripStatus.ACTIVE)).thenReturn(true);

        assertThatThrownBy(() -> tripService.startTrip(2L, new StartTripRequest(201L)))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("This bus already has an active trip");
    }

    @Test
    void startTripRejectsDriverWithActiveTrip() {
        when(tripRepository.existsByBusIdAndStatus(201L, TripStatus.ACTIVE)).thenReturn(false);
        when(tripRepository.existsByDriverIdAndStatus(2L, TripStatus.ACTIVE)).thenReturn(true);

        assertThatThrownBy(() -> tripService.startTrip(2L, new StartTripRequest(201L)))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("This driver already has an active trip");
    }

    @Test
    void completeTripMarksTripCompleted() {
        Trip trip = Trip.builder()
                .id(301L)
                .busId(201L)
                .driverId(2L)
                .startTime(Instant.parse("2026-05-09T06:00:00Z"))
                .status(TripStatus.ACTIVE)
                .build();
        when(tripRepository.findByIdAndDriverId(301L, 2L)).thenReturn(Optional.of(trip));

        TripResponse response = tripService.completeTrip(2L, 301L);

        assertThat(response.status()).isEqualTo("COMPLETED");
        assertThat(response.endTime()).isNotNull();
        assertThat(trip.getStatus()).isEqualTo(TripStatus.COMPLETED);
    }

    @Test
    void completeTripRejectsAlreadyCompletedTrip() {
        Trip trip = Trip.builder()
                .id(301L)
                .busId(201L)
                .driverId(2L)
                .startTime(Instant.parse("2026-05-09T06:00:00Z"))
                .endTime(Instant.parse("2026-05-09T06:30:00Z"))
                .status(TripStatus.COMPLETED)
                .build();
        when(tripRepository.findByIdAndDriverId(301L, 2L)).thenReturn(Optional.of(trip));

        assertThatThrownBy(() -> tripService.completeTrip(2L, 301L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("This trip is already completed");
    }

    @Test
    void completeTripThrowsWhenTripDoesNotBelongToDriver() {
        when(tripRepository.findByIdAndDriverId(301L, 2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tripService.completeTrip(2L, 301L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Active trip not found: 301");
    }
}
