package com.sanrio.stopservice.stop.config;

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
    CommandLineRunner seedStops() {
        return args -> {
            jdbcTemplate.update("""
                    insert into stops (id, route_id, stop_name, latitude, longitude, sequence_no)
                    values
                        (1001, 101, 'Main Gate', 2.94410, 101.87410, 1),
                        (1002, 101, 'Library Square', 2.94520, 101.87540, 2),
                        (1003, 101, 'Student Center', 2.94610, 101.87620, 3),
                        (1004, 101, 'Sports Complex', 2.94700, 101.87710, 4),
                        (1005, 102, 'Hostel A', 2.94120, 101.87130, 1),
                        (1006, 102, 'Hostel B', 2.94200, 101.87210, 2),
                        (1007, 102, 'Food Court', 2.94330, 101.87320, 3),
                        (1008, 102, 'Central Campus', 2.94500, 101.87500, 4),
                        (1009, 103, 'Engineering Gate', 2.94820, 101.87850, 1),
                        (1010, 103, 'Workshop Block', 2.94910, 101.87930, 2),
                        (1011, 103, 'Innovation Lab', 2.95000, 101.88010, 3),
                        (1012, 103, 'Lecture Hall E', 2.95100, 101.88100, 4)
                    on conflict (id) do nothing
                    """);
            jdbcTemplate.queryForObject("select setval(pg_get_serial_sequence('stops', 'id'), greatest((select max(id) from stops), 1))", Long.class);
        };
    }
}
