package com.sanrio.routeservice.route.service;

import com.sanrio.routeservice.common.BadRequestException;
import com.sanrio.routeservice.common.ResourceNotFoundException;
import com.sanrio.routeservice.route.dto.CreateRouteRequest;
import com.sanrio.routeservice.route.dto.RouteResponse;
import com.sanrio.routeservice.route.dto.UpdateRouteRequest;
import com.sanrio.routeservice.route.entity.Route;
import com.sanrio.routeservice.route.repository.RouteRepository;
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
class RouteServiceTest {
    @Mock
    private RouteRepository routeRepository;

    @InjectMocks
    private RouteService routeService;

    @Test
    void createRouteSavesUniqueRoute() {
        CreateRouteRequest request = new CreateRouteRequest("Campus Loop A", "Main campus route");
        when(routeRepository.existsByRouteName(request.routeName())).thenReturn(false);
        when(routeRepository.save(any(Route.class))).thenAnswer(invocation -> {
            Route route = invocation.getArgument(0);
            route.setId(101L);
            return route;
        });

        RouteResponse response = routeService.createRoute(request);

        assertThat(response.id()).isEqualTo(101L);
        assertThat(response.routeName()).isEqualTo("Campus Loop A");
        verify(routeRepository).save(any(Route.class));
    }

    @Test
    void createRouteRejectsDuplicateName() {
        CreateRouteRequest request = new CreateRouteRequest("Campus Loop A", "Duplicate");
        when(routeRepository.existsByRouteName(request.routeName())).thenReturn(true);

        assertThatThrownBy(() -> routeService.createRoute(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Route name already exists");
    }

    @Test
    void getRouteThrowsWhenMissing() {
        when(routeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> routeService.getRoute(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Route not found: 999");
    }

    @Test
    void getRoutesMapsAllRoutes() {
        when(routeRepository.findAll()).thenReturn(List.of(
                Route.builder().id(101L).routeName("Campus Loop A").description("Main campus route").build(),
                Route.builder().id(102L).routeName("Hostel Express").description("Hostel route").build()
        ));

        List<RouteResponse> responses = routeService.getRoutes();

        assertThat(responses).extracting(RouteResponse::id).containsExactly(101L, 102L);
    }

    @Test
    void updateRouteChangesExistingRoute() {
        Route route = Route.builder().id(101L).routeName("Campus Loop A").description("Old description").build();
        UpdateRouteRequest request = new UpdateRouteRequest("Campus Loop Updated", "Updated description");
        when(routeRepository.findById(101L)).thenReturn(Optional.of(route));
        when(routeRepository.existsByRouteNameAndIdNot(request.routeName(), 101L)).thenReturn(false);
        when(routeRepository.save(route)).thenReturn(route);

        RouteResponse response = routeService.updateRoute(101L, request);

        assertThat(response.routeName()).isEqualTo("Campus Loop Updated");
        assertThat(response.description()).isEqualTo("Updated description");
    }

    @Test
    void deleteRouteRemovesExistingRoute() {
        Route route = Route.builder().id(101L).routeName("Campus Loop A").description("Main campus route").build();
        when(routeRepository.findById(101L)).thenReturn(Optional.of(route));

        routeService.deleteRoute(101L);

        verify(routeRepository).delete(route);
    }
}
