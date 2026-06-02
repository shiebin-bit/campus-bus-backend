package com.sanrio.busservice.bus.repository;

import com.sanrio.busservice.bus.entity.Bus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusRepository extends JpaRepository<Bus, Long> {
    boolean existsByBusCode(String busCode);
    boolean existsByPlateNumber(String plateNumber);
    boolean existsByBusCodeAndIdNot(String busCode, Long id);
    boolean existsByPlateNumberAndIdNot(String plateNumber, Long id);
}
