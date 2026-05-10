package com.sanrio.tripservice.trip.repository;

import com.sanrio.tripservice.trip.entity.Trip;
import com.sanrio.tripservice.trip.entity.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Long> {
    boolean existsByBusIdAndStatus(Long busId, TripStatus status);
    boolean existsByDriverIdAndStatus(Long driverId, TripStatus status);
    Optional<Trip> findByIdAndDriverId(Long id, Long driverId);
}
