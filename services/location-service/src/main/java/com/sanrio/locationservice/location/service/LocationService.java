package com.sanrio.locationservice.location.service;

import com.sanrio.locationservice.location.dto.CreateLocationRequest;
import com.sanrio.locationservice.location.dto.LiveBusResponse;
import com.sanrio.locationservice.location.dto.LocationResponse;
import com.sanrio.locationservice.location.entity.BusLocation;
import com.sanrio.locationservice.location.repository.BusLocationRepository;
import com.sanrio.locationservice.location.websocket.LiveLocationBroadcaster;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationService {
    private final BusLocationRepository busLocationRepository;
    private final LiveLocationBroadcaster liveLocationBroadcaster;

    @Transactional
    public LocationResponse saveLocation(Long driverId, CreateLocationRequest request) {
        BusLocation location = busLocationRepository.save(BusLocation.builder()
                .tripId(request.tripId())
                .busId(request.busId())
                .driverId(driverId)
                .latitude(request.latitude())
                .longitude(request.longitude())
                .recordedAt(Instant.now())
                .build());
        LiveBusResponse liveBusResponse = toLiveBusResponse(location);
        liveLocationBroadcaster.broadcastLiveLocation(liveBusResponse);
        return toLocationResponse(location);
    }

    public List<LiveBusResponse> getLiveBuses() {
        return busLocationRepository.findLatestLocationsPerTrip().stream().map(this::toLiveBusResponse).toList();
    }

    private LocationResponse toLocationResponse(BusLocation location) {
        return new LocationResponse(location.getId(), location.getTripId(), location.getBusId(), location.getDriverId(), location.getLatitude(), location.getLongitude(), location.getRecordedAt());
    }

    private LiveBusResponse toLiveBusResponse(BusLocation location) {
        return new LiveBusResponse(location.getTripId(), location.getBusId(), location.getLatitude(), location.getLongitude(), location.getRecordedAt());
    }
}
