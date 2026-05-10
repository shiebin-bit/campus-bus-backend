package com.sanrio.locationservice.location.service;

import com.sanrio.locationservice.location.dto.CreateLocationRequest;
import com.sanrio.locationservice.location.dto.LiveBusResponse;
import com.sanrio.locationservice.location.dto.LocationResponse;
import com.sanrio.locationservice.location.entity.BusLocation;
import com.sanrio.locationservice.location.repository.BusLocationRepository;
import com.sanrio.locationservice.location.websocket.LiveLocationBroadcaster;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocationServiceTest {
    @Mock
    private BusLocationRepository busLocationRepository;

    @Mock
    private LiveLocationBroadcaster liveLocationBroadcaster;

    @InjectMocks
    private LocationService locationService;

    @Test
    void saveLocationStoresDriverLocation() {
        CreateLocationRequest request = new CreateLocationRequest(301L, 201L, 2.9470, 101.8771);
        when(busLocationRepository.save(any(BusLocation.class))).thenAnswer(invocation -> {
            BusLocation location = invocation.getArgument(0);
            location.setId(3001L);
            return location;
        });

        LocationResponse response = locationService.saveLocation(2L, request);

        assertThat(response.id()).isEqualTo(3001L);
        assertThat(response.tripId()).isEqualTo(301L);
        assertThat(response.busId()).isEqualTo(201L);
        assertThat(response.driverId()).isEqualTo(2L);
        assertThat(response.recordedAt()).isNotNull();
        verify(busLocationRepository).save(any(BusLocation.class));
        verify(liveLocationBroadcaster).broadcastLiveLocation(any());
    }

    @Test
    void getLiveBusesReturnsLatestLocationsPerTrip() {
        when(busLocationRepository.findLatestLocationsPerTrip()).thenReturn(List.of(
                BusLocation.builder()
                        .id(3004L)
                        .tripId(301L)
                        .busId(201L)
                        .driverId(2L)
                        .latitude(2.9470)
                        .longitude(101.8771)
                        .recordedAt(Instant.parse("2026-05-09T06:30:00Z"))
                        .build()
        ));

        List<LiveBusResponse> responses = locationService.getLiveBuses();

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().tripId()).isEqualTo(301L);
        assertThat(responses.getFirst().busId()).isEqualTo(201L);
    }
}
