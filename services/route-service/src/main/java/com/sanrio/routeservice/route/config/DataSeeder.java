package com.sanrio.routeservice.route.config;

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
    CommandLineRunner seedRoutes() {
        return args -> {
            jdbcTemplate.update("""
                    insert into routes (id, route_name, description)
                    values
                        (101, 'Campus Loop A', 'Main campus circular route covering academic blocks and library'),
                        (102, 'Hostel Express', 'Direct route between student hostels and central campus'),
                        (103, 'Engineering Shuttle', 'Shuttle route for engineering faculty and labs')
                    on conflict (id) do nothing
                    """);
            jdbcTemplate.queryForObject("select setval(pg_get_serial_sequence('routes', 'id'), greatest((select max(id) from routes), 1))", Long.class);
        };
    }
}
