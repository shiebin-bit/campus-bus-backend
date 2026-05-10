package com.sanrio.locationservice.location.config;

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
    CommandLineRunner seedLocations() {
        return args -> {
            jdbcTemplate.update("""
                    insert into bus_locations (id, trip_id, bus_id, driver_id, latitude, longitude, recorded_at)
                    values
                        (3001, 301, 201, 2, 2.94410, 101.87410, now() - interval '12 minutes'),
                        (3002, 301, 201, 2, 2.94520, 101.87540, now() - interval '8 minutes'),
                        (3003, 301, 201, 2, 2.94610, 101.87620, now() - interval '4 minutes'),
                        (3004, 301, 201, 2, 2.94700, 101.87710, now())
                    on conflict (id) do nothing
                    """);
            jdbcTemplate.queryForObject("select setval(pg_get_serial_sequence('bus_locations', 'id'), greatest((select max(id) from bus_locations), 1))", Long.class);
        };
    }
}
