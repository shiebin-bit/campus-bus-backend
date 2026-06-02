package com.sanrio.busservice.bus.service;

import com.sanrio.busservice.bus.dto.BusResponse;
import com.sanrio.busservice.bus.dto.CreateBusRequest;
import com.sanrio.busservice.bus.dto.UpdateBusRequest;
import com.sanrio.busservice.bus.entity.Bus;
import com.sanrio.busservice.bus.repository.BusRepository;
import com.sanrio.busservice.common.BadRequestException;
import com.sanrio.busservice.common.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BusService {
    private final BusRepository busRepository;

    @Transactional
    public BusResponse createBus(CreateBusRequest request) {
        if (busRepository.existsByBusCode(request.busCode())) {
            throw new BadRequestException("Bus code already exists");
        }
        if (busRepository.existsByPlateNumber(request.plateNumber())) {
            throw new BadRequestException("Plate number already exists");
        }
        Bus bus = busRepository.save(Bus.builder().busCode(request.busCode()).plateNumber(request.plateNumber()).routeId(request.routeId()).build());
        return toResponse(bus);
    }

    public List<BusResponse> getBuses() {
        return busRepository.findAll().stream().map(this::toResponse).toList();
    }

    public BusResponse getBus(Long busId) {
        return toResponse(findBus(busId));
    }

    @Transactional
    public BusResponse updateBus(Long busId, UpdateBusRequest request) {
        Bus bus = findBus(busId);
        if (busRepository.existsByBusCodeAndIdNot(request.busCode(), busId)) {
            throw new BadRequestException("Bus code already exists");
        }
        if (busRepository.existsByPlateNumberAndIdNot(request.plateNumber(), busId)) {
            throw new BadRequestException("Plate number already exists");
        }
        bus.setBusCode(request.busCode());
        bus.setPlateNumber(request.plateNumber());
        bus.setRouteId(request.routeId());
        return toResponse(busRepository.save(bus));
    }

    @Transactional
    public void deleteBus(Long busId) {
        busRepository.delete(findBus(busId));
    }

    private Bus findBus(Long busId) {
        return busRepository.findById(busId)
                .orElseThrow(() -> new ResourceNotFoundException("Bus not found: " + busId));
    }

    private BusResponse toResponse(Bus bus) {
        return new BusResponse(bus.getId(), bus.getBusCode(), bus.getPlateNumber(), bus.getRouteId());
    }
}
