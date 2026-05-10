package com.sanrio.tripservice.trip.config;

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
    CommandLineRunner seedTrips() {
        return args -> {
            jdbcTemplate.update("""
                    insert into trips (id, bus_id, driver_id, start_time, status)
                    values (301, 201, 2, now() - interval '15 minutes', 'ACTIVE')
                    on conflict (id) do nothing
                    """);
            jdbcTemplate.queryForObject("select setval(pg_get_serial_sequence('trips', 'id'), greatest((select max(id) from trips), 1))", Long.class);
        };
    }
}
