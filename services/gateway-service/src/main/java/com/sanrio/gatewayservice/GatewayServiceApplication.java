package com.sanrio.gatewayservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@SpringBootApplication
public class GatewayServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }

    @Bean
    RouteLocator campusBusRoutes(
            RouteLocatorBuilder builder,
            @Value("${app.services.auth-url}") String authUrl,
            @Value("${app.services.route-url}") String routeUrl,
            @Value("${app.services.stop-url}") String stopUrl,
            @Value("${app.services.bus-url}") String busUrl,
            @Value("${app.services.trip-url}") String tripUrl,
            @Value("${app.services.location-url}") String locationUrl,
            @Value("${app.services.location-ws-url}") String locationWsUrl
    ) {
        return builder.routes()
                .route("auth-service", route -> route.path("/api/auth/**").uri(authUrl))
                .route("location-service-websocket", route -> route.path("/ws/locations/live").uri(locationWsUrl))
                .route("stop-service-route-stops", route -> route.path("/api/routes/*/stops").uri(stopUrl))
                .route("route-service", route -> route.path("/api/routes/**").uri(routeUrl))
                .route("stop-service", route -> route.path("/api/stops/**").uri(stopUrl))
                .route("location-service-live-buses", route -> route.path("/api/buses/live").uri(locationUrl))
                .route("bus-service", route -> route.path("/api/buses/**").uri(busUrl))
                .route("trip-service", route -> route.path("/api/trips/**").uri(tripUrl))
                .route("location-service", route -> route.path("/api/locations/**").uri(locationUrl))
                .build();
    }

    @Bean
    CorsWebFilter corsWebFilter(@Value("${app.cors.allowed-origins}") List<String> allowedOrigins) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return new CorsWebFilter(source);
    }
}
