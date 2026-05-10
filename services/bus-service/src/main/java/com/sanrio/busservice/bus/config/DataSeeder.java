package com.sanrio.busservice.bus.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {
    private final JdbcTemplate jdbcTemplate;

    @Bean
    CommandLineRunner seedBuses() {
        return args -> {
            jdbcTemplate.update("""
                    insert into buses (id, bus_code, plate_number, route_id)
                    values
                        (201, 'BUS-A01', 'CB-A0101', 101),
                        (202, 'BUS-A02', 'CB-A0102', 101),
                        (203, 'BUS-H01', 'CB-H0101', 102),
                        (204, 'BUS-E01', 'CB-E0101', 103),
                        (205, 'BUS-M01', 'CB-M0101', 104),
                        (206, 'BUS-N01', 'CB-N0101', 105),
                        (207, 'BUS-E02', 'CB-E0102', 103)
                    on conflict (id) do nothing
                    """);
            jdbcTemplate.queryForObject("select setval(pg_get_serial_sequence('buses', 'id'), greatest((select max(id) from buses), 1))", Long.class);
        };
    }
}
