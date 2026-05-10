package com.sanrio.stopservice.stop.service;

import com.sanrio.stopservice.common.BadRequestException;
import com.sanrio.stopservice.stop.dto.CreateStopRequest;
import com.sanrio.stopservice.stop.dto.StopResponse;
import com.sanrio.stopservice.stop.entity.Stop;
import com.sanrio.stopservice.stop.repository.StopRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StopServiceTest {
    @Mock
    private StopRepository stopRepository;

    @InjectMocks
    private StopService stopService;

    @Test
    void createStopSavesWhenSequenceIsAvailable() {
        CreateStopRequest request = new CreateStopRequest(101L, "Library Square", 2.9452, 101.8754, 2);
        when(stopRepository.existsByRouteIdAndSequenceNo(101L, 2)).thenReturn(false);
        when(stopRepository.save(any(Stop.class))).thenAnswer(invocation -> {
            Stop stop = invocation.getArgument(0);
            stop.setId(1002L);
            return stop;
        });

        StopResponse response = stopService.createStop(request);

        assertThat(response.id()).isEqualTo(1002L);
        assertThat(response.sequenceNo()).isEqualTo(2);
        verify(stopRepository).save(any(Stop.class));
    }

    @Test
    void createStopRejectsDuplicateSequenceForRoute() {
        CreateStopRequest request = new CreateStopRequest(101L, "Duplicate Stop", 2.0, 101.0, 1);
        when(stopRepository.existsByRouteIdAndSequenceNo(101L, 1)).thenReturn(true);

        assertThatThrownBy(() -> stopService.createStop(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Sequence number already exists for this route");
    }

    @Test
    void getStopsByRouteReturnsRepositoryOrder() {
        when(stopRepository.findByRouteIdOrderBySequenceNoAsc(101L)).thenReturn(List.of(
                Stop.builder().id(1001L).routeId(101L).stopName("Main Gate").latitude(2.9441).longitude(101.8741).sequenceNo(1).build(),
                Stop.builder().id(1002L).routeId(101L).stopName("Library Square").latitude(2.9452).longitude(101.8754).sequenceNo(2).build()
        ));

        List<StopResponse> responses = stopService.getStopsByRoute(101L);

        assertThat(responses).extracting(StopResponse::sequenceNo).containsExactly(1, 2);
    }
}
