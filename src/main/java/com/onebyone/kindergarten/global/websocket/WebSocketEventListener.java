package com.onebyone.kindergarten.global.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final WebSocketConnectionManager connectionManager;

    @EventListener
    public void handleConnect(SessionConnectEvent event) {

        StompHeaderAccessor accessor =
            StompHeaderAccessor.wrap(event.getMessage());

        Authentication auth = (Authentication) accessor.getUser();
        Long userId = Long.valueOf(auth.getName());
        String sessionId = accessor.getSessionId();

        connectionManager.connect(userId, sessionId);
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {

        StompHeaderAccessor accessor =
            StompHeaderAccessor.wrap(event.getMessage());

        Authentication auth = (Authentication) accessor.getUser();
        if (auth == null) return;

        Long userId = Long.valueOf(auth.getName());
        String sessionId = accessor.getSessionId();

        connectionManager.disconnect(userId, sessionId);
    }
}
