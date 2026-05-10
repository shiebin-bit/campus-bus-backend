package com.sanrio.stopservice.stop.repository;

import com.sanrio.stopservice.stop.entity.Stop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StopRepository extends JpaRepository<Stop, Long> {
    List<Stop> findByRouteIdOrderBySequenceNoAsc(Long routeId);
    boolean existsByRouteIdAndSequenceNo(Long routeId, Integer sequenceNo);
}
