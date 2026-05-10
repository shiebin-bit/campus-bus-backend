package com.sanrio.busservice.bus.service;

import com.sanrio.busservice.bus.dto.BusResponse;
import com.sanrio.busservice.bus.dto.CreateBusRequest;
import com.sanrio.busservice.bus.entity.Bus;
import com.sanrio.busservice.bus.repository.BusRepository;
import com.sanrio.busservice.common.BadRequestException;
import com.sanrio.busservice.common.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BusServiceTest {
    @Mock
    private BusRepository busRepository;

    @InjectMocks
    private BusService busService;

    @Test
    void createBusSavesWhenCodeAndPlateAreUnique() {
        CreateBusRequest request = new CreateBusRequest("BUS-A01", "WAA 201", 101L);
        when(busRepository.existsByBusCode(request.busCode())).thenReturn(false);
        when(busRepository.existsByPlateNumber(request.plateNumber())).thenReturn(false);
        when(busRepository.save(any(Bus.class))).thenAnswer(invocation -> {
            Bus bus = invocation.getArgument(0);
            bus.setId(201L);
            return bus;
        });

        BusResponse response = busService.createBus(request);

        assertThat(response.id()).isEqualTo(201L);
        assertThat(response.busCode()).isEqualTo("BUS-A01");
        verify(busRepository).save(any(Bus.class));
    }

    @Test
    void createBusRejectsDuplicateBusCode() {
        CreateBusRequest request = new CreateBusRequest("BUS-A01", "WAA 201", 101L);
        when(busRepository.existsByBusCode(request.busCode())).thenReturn(true);

        assertThatThrownBy(() -> busService.createBus(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Bus code already exists");
    }

    @Test
    void createBusRejectsDuplicatePlateNumber() {
        CreateBusRequest request = new CreateBusRequest("BUS-A02", "WAA 201", 101L);
        when(busRepository.existsByBusCode(request.busCode())).thenReturn(false);
        when(busRepository.existsByPlateNumber(request.plateNumber())).thenReturn(true);

        assertThatThrownBy(() -> busService.createBus(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Plate number already exists");
    }

    @Test
    void getBusThrowsWhenMissing() {
        when(busRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> busService.getBus(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Bus not found: 999");
    }

    @Test
    void getBusesMapsAllBuses() {
        when(busRepository.findAll()).thenReturn(List.of(
                Bus.builder().id(201L).busCode("BUS-A01").plateNumber("WAA 201").routeId(101L).build(),
                Bus.builder().id(202L).busCode("BUS-A02").plateNumber("WAA 202").routeId(101L).build()
        ));

        List<BusResponse> responses = busService.getBuses();

        assertThat(responses).extracting(BusResponse::busCode).containsExactly("BUS-A01", "BUS-A02");
    }
}
