package com.sanrio.authservice.auth.config;

import com.sanrio.authservice.auth.entity.Role;
import com.sanrio.authservice.auth.entity.User;
import com.sanrio.authservice.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner seedUsers() {
        return args -> {
            seedUser("System Admin", "admin@campusbus.com", "Admin123!", Role.ADMIN);
            seedUser("Campus Driver", "driver@campusbus.com", "Driver123!", Role.DRIVER);
            seedUser("Nora Campus Driver", "nora.driver@campusbus.com", "Driver123!", Role.DRIVER);
            seedUser("Ravi Campus Driver", "ravi.driver@campusbus.com", "Driver123!", Role.DRIVER);
            seedUser("Aina Campus Driver", "aina.driver@campusbus.com", "Driver123!", Role.DRIVER);
        };
    }

    private void seedUser(String name, String email, String password, Role role) {
        if (userRepository.existsByEmail(email)) {
            return;
        }
        userRepository.save(User.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(role)
                .build());
    }
}
