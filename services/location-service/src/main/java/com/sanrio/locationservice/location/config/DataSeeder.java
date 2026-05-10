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
                        (3001, 301, 201, 3, 2.94410, 101.87410, now() - interval '16 minutes'),
                        (3002, 301, 201, 3, 2.94520, 101.87540, now() - interval '11 minutes'),
                        (3003, 301, 201, 3, 2.94610, 101.87620, now() - interval '6 minutes'),
                        (3004, 301, 201, 3, 2.94700, 101.87710, now() - interval '1 minutes'),
                        (3005, 302, 203, 4, 2.94120, 101.87130, now() - interval '10 minutes'),
                        (3006, 302, 203, 4, 2.94200, 101.87210, now() - interval '7 minutes'),
                        (3007, 302, 203, 4, 2.94270, 101.87270, now() - interval '4 minutes'),
                        (3008, 302, 203, 4, 2.94330, 101.87320, now() - interval '30 seconds'),
                        (3009, 303, 205, 5, 2.94470, 101.87940, now() - interval '8 minutes'),
                        (3010, 303, 205, 5, 2.94560, 101.88020, now() - interval '5 minutes'),
                        (3011, 303, 205, 5, 2.94660, 101.88090, now() - interval '2 minutes'),
                        (3012, 303, 205, 5, 2.94740, 101.88160, now())
                    on conflict (id) do nothing
                    """);
            jdbcTemplate.queryForObject("select setval(pg_get_serial_sequence('bus_locations', 'id'), greatest((select max(id) from bus_locations), 1))", Long.class);
        };
    }
}
