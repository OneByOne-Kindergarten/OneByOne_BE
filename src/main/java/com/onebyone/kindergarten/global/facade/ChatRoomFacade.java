package com.onebyone.kindergarten.global.facade;

import com.onebyone.kindergarten.domain.user.service.UserService;
import com.onebyone.kindergarten.domain.websocket.dto.ChatRoomCreateRequest;
import com.onebyone.kindergarten.domain.websocket.dto.ChatRoomResponse;
import com.onebyone.kindergarten.domain.websocket.service.ChatRoomService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChatRoomFacade {
    private final UserService userService;
    private final ChatRoomService chatRoomService;


    @Transactional
    public ChatRoomResponse createRoom(Long userId, ChatRoomCreateRequest request) {
        userService.validateUserStatus(userId, request.getUserIds());
        return chatRoomService.createRoom(userId, request);
    }
}
