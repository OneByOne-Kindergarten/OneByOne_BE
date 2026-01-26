package com.onebyone.kindergarten.domain.websocket.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;

    public void save(Long chatRoomId, Long userId, String message) {

    }
}
