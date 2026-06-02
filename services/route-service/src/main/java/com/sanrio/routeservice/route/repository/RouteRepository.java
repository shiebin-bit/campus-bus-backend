package com.sanrio.routeservice.route.repository;

import com.sanrio.routeservice.route.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRepository extends JpaRepository<Route, Long> {
    boolean existsByRouteName(String routeName);
    boolean existsByRouteNameAndIdNot(String routeName, Long id);
}
