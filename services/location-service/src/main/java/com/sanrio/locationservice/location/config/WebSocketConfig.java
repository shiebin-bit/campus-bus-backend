package com.sanrio.locationservice.location.config;

import com.sanrio.locationservice.location.websocket.LiveLocationWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    private final LiveLocationWebSocketHandler liveLocationWebSocketHandler;

    @Value("${app.cors.allowed-origins:http://localhost:4200,http://localhost:5500,http://localhost:8100,http://127.0.0.1:4200,http://127.0.0.1:5500,http://127.0.0.1:8100}")
    private String[] allowedOrigins;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(liveLocationWebSocketHandler, "/ws/locations/live")
                .setAllowedOrigins(allowedOrigins);
    }
}
