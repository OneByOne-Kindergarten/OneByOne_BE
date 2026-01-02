package com.onebyone.kindergarten.global.websocket;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketConnectionManager {
    private final Map<Long, Set<String>> connections = new ConcurrentHashMap<>();
    private final int MAX_CONNECTION_PER_USER = 1;

    public synchronized void connect(Long userId, String sessionId) {

        connections.putIfAbsent(userId, ConcurrentHashMap.newKeySet());

        Set<String> sessions = connections.get(userId);

//        if (sessions.size() >= MAX_CONNECTION_PER_USER) {
//            throw new BusinessException(ErrorCodes.WEBSOCKET_CONNECTION_LIMIT_EXCEEDED);
//        }

        sessions.add(sessionId);
    }

    public synchronized void disconnect(Long userId, String sessionId) {

        Set<String> sessions = connections.get(userId);
        if (sessions == null) return;

        sessions.remove(sessionId);

        if (sessions.isEmpty()) {
            connections.remove(userId);
        }
    }

    public boolean isConnected(Long userId) {
        return connections.containsKey(userId);
    }
}
