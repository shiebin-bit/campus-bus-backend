package com.sanrio.locationservice.location.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.sanrio.locationservice.common.ApiResponse;
import com.sanrio.locationservice.location.dto.LiveBusResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LiveLocationBroadcaster {
    private final LiveLocationWebSocketHandler webSocketHandler;
    private final ObjectMapper objectMapper = JsonMapper.builder()
            .findAndAddModules()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();

    public LiveLocationBroadcaster(LiveLocationWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    public void broadcastLiveLocation(LiveBusResponse liveBusResponse) {
        try {
            String message = objectMapper.writeValueAsString(new ApiResponse<>("Live bus location updated", liveBusResponse));
            webSocketHandler.broadcast(message);
        } catch (Exception exception) {
            log.warn("Failed to broadcast live bus location update", exception);
        }
    }
}
