package com.onebyone.kindergarten.global.facade;

import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.domain.user.service.UserService;
import com.onebyone.kindergarten.domain.websocket.dto.ChatRoomCreateRequest;
import com.onebyone.kindergarten.domain.websocket.dto.ChatRoomLastMessageDto;
import com.onebyone.kindergarten.domain.websocket.dto.ChatRoomResponse;
import com.onebyone.kindergarten.domain.websocket.service.ChatMessageService;
import com.onebyone.kindergarten.domain.websocket.service.ChatRoomService;
import com.onebyone.kindergarten.global.exception.BusinessException;
import com.onebyone.kindergarten.global.exception.ErrorCodes;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChatRoomFacade {
    private final UserService userService;
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    @Transactional
    public ChatRoomResponse createRoom(Long userId, ChatRoomCreateRequest request) {
        if (request.getUserIds() == null || request.getUserIds().isEmpty()) {
            throw new BusinessException(ErrorCodes.INVALID_CHAT_ROOM_MEMBER);
        }

        Optional<ChatRoomResponse> existing =
                chatRoomService.findExistingRoom(userId, request);

        if (existing.isPresent()) {
            return existing.get();
        }

        userService.validateUserStatus(userId, request.getUserIds());

        return chatRoomService.createRoom(userId, request);
    }

    public List<ChatRoomLastMessageDto> findParticipatedRooms(Long userId) {
        User user = userService.getUserById(userId);
        return chatRoomService.findParticipatedRooms(user.getId());
    }

    @Transactional
    public void saveChat(Long chatRoomId, Long userId, String message) {
        chatMessageService.save(chatRoomId, userId, message);

        // 2️⃣ 구독자들에게 PUSH
        messagingTemplate.convertAndSend(
                "/topic/chat/" + chatRoomId,
                message
        );
    }
}
