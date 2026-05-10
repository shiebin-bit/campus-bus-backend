package com.sanrio.gatewayservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;

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
    WebFilter corsPreflightFilter(@Value("${app.cors.allowed-origins}") List<String> allowedOrigins) {
        return (exchange, chain) -> {
            if (!isPreflightRequest(exchange)) {
                return chain.filter(exchange);
            }

            String origin = exchange.getRequest().getHeaders().getOrigin();
            if (origin != null && allowedOrigins.contains(origin)) {
                HttpHeaders headers = exchange.getResponse().getHeaders();
                headers.setAccessControlAllowOrigin(origin);
                headers.setAccessControlAllowMethods(List.of(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT,
                        HttpMethod.PATCH, HttpMethod.DELETE, HttpMethod.OPTIONS));
                headers.setAccessControlAllowHeaders(exchange.getRequest().getHeaders().getAccessControlRequestHeaders());
                headers.setAccessControlExposeHeaders(List.of(HttpHeaders.AUTHORIZATION));
            }

            exchange.getResponse().setStatusCode(HttpStatus.OK);
            return exchange.getResponse().setComplete();
        };
    }

    private boolean isPreflightRequest(ServerWebExchange exchange) {
        return exchange.getRequest().getMethod() == HttpMethod.OPTIONS
                && exchange.getRequest().getHeaders().getOrigin() != null
                && exchange.getRequest().getHeaders().getAccessControlRequestMethod() != null;
    }
}
