package com.sanrio.stopservice.stop.service;

import com.sanrio.stopservice.common.BadRequestException;
import com.sanrio.stopservice.stop.dto.CreateStopRequest;
import com.sanrio.stopservice.stop.dto.StopResponse;
import com.sanrio.stopservice.stop.entity.Stop;
import com.sanrio.stopservice.stop.repository.StopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StopService {
    private final StopRepository stopRepository;

    @Transactional
    public StopResponse createStop(CreateStopRequest request) {
        if (stopRepository.existsByRouteIdAndSequenceNo(request.routeId(), request.sequenceNo())) {
            throw new BadRequestException("Sequence number already exists for this route");
        }
        Stop stop = stopRepository.save(Stop.builder()
                .routeId(request.routeId())
                .stopName(request.stopName())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .sequenceNo(request.sequenceNo())
                .build());
        return toResponse(stop);
    }

    public List<StopResponse> getStopsByRoute(Long routeId) {
        return stopRepository.findByRouteIdOrderBySequenceNoAsc(routeId).stream().map(this::toResponse).toList();
    }

    private StopResponse toResponse(Stop stop) {
        return new StopResponse(stop.getId(), stop.getRouteId(), stop.getStopName(), stop.getLatitude(), stop.getLongitude(), stop.getSequenceNo());
    }
}
