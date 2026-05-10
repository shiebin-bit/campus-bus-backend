package com.sanrio.locationservice.location.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class LiveLocationWebSocketHandler extends TextWebSocketHandler {
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        log.info("Live location WebSocket connected: {}", session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        log.info("Live location WebSocket disconnected: {}", session.getId());
    }

    void broadcast(String message) {
        sessions.removeIf(session -> !session.isOpen());
        for (WebSocketSession session : sessions) {
            send(session, message);
        }
    }

    private void send(WebSocketSession session, String message) {
        try {
            session.sendMessage(new TextMessage(message));
        } catch (Exception exception) {
            log.warn("Failed to send live location update to WebSocket session {}", session.getId(), exception);
        }
    }
}
