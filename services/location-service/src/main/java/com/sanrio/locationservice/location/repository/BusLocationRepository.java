package com.sanrio.locationservice.location.repository;

import com.sanrio.locationservice.location.entity.BusLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BusLocationRepository extends JpaRepository<BusLocation, Long> {
    @Query("""
            select location from BusLocation location
            where location.recordedAt = (
                select max(innerLocation.recordedAt)
                from BusLocation innerLocation
                where innerLocation.tripId = location.tripId
            )
            order by location.recordedAt desc
            """)
    List<BusLocation> findLatestLocationsPerTrip();
}
