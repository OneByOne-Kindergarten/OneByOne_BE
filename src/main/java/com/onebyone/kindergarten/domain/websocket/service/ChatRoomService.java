package com.onebyone.kindergarten.domain.websocket.service;

import com.onebyone.kindergarten.domain.websocket.domain.ChatRoom;
import com.onebyone.kindergarten.domain.websocket.domain.ChatRoomMember;
import com.onebyone.kindergarten.domain.websocket.dto.ChatRoomCreateRequest;
import com.onebyone.kindergarten.domain.websocket.dto.ChatRoomLastMessageDto;
import com.onebyone.kindergarten.domain.websocket.dto.ChatRoomResponse;
import com.onebyone.kindergarten.domain.websocket.dto.RoomType;
import com.onebyone.kindergarten.domain.websocket.repository.ChatRoomMemberRepository;
import com.onebyone.kindergarten.domain.websocket.repository.ChatRoomRepository;
import com.onebyone.kindergarten.domain.websocket.util.ChatRoomHashUtil;
import com.onebyone.kindergarten.global.exception.BusinessException;
import com.onebyone.kindergarten.global.exception.ErrorCodes;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    Logger logger = LoggerFactory.getLogger(ChatRoomService.class);

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;

    public ChatRoomResponse createRoom(Long userId, ChatRoomCreateRequest request) {
        Optional<ChatRoomResponse> existing =
                findExistingRoom(userId, request);

        if (existing.isPresent()) {
            logger.info("========== 기존 방 반환 ==========");
            return existing.get();
        }

        if (request.getType() == RoomType.PRIVATE) {
            logger.info("========== 1대1 대화방 ==========");
            validatePrivate(request, userId);
        } else {
            logger.info("========== 그룹 대화방 ==========");
            validateGroup(request, userId);
        }

        return createRoomInternal(userId, request);
    }

    private void validatePrivate(ChatRoomCreateRequest request, Long userId) {
        if (request.getUserIds().size() != 1) {
            throw new BusinessException(ErrorCodes.INVALID_CHAT_ROOM_MEMBER);
        }

        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new BusinessException(ErrorCodes.INVALID_CHAT_ROOM_TITLE);
        }

        if (request.getUserIds().contains(userId)) {
            throw new BusinessException(ErrorCodes.INVALID_CHAT_SELF);
        }
    }

    private void validateGroup(ChatRoomCreateRequest request, Long userId) {
        if (request.getUserIds().size() < 2) {
            throw new BusinessException(ErrorCodes.INVALID_CHAT_ROOM_MEMBER);
        }

        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new BusinessException(ErrorCodes.INVALID_CHAT_ROOM_TITLE);
        }

        if (request.getUserIds().contains(userId)) {
            throw new BusinessException(ErrorCodes.INVALID_CHAT_SELF);
        }
    }

    public Optional<ChatRoomResponse> findExistingRoom(
            Long userId,
            ChatRoomCreateRequest request
    ) {
        Set<Long> members = new HashSet<>();
        members.add(userId);
        members.addAll(request.getUserIds());

        String hash = ChatRoomHashUtil.generate(members);

        return chatRoomRepository.findByMemberHash(hash)
                .map(room ->
                        new ChatRoomResponse(
                                room.getId(),
                                room.getType()
                        )
                );
    }

    private ChatRoomResponse createRoomInternal(
            Long userId,
            ChatRoomCreateRequest request
    ) {
        Set<Long> members = new HashSet<>();
        members.add(userId);
        members.addAll(request.getUserIds());

        String hash = ChatRoomHashUtil.generate(members);

        ChatRoom room = ChatRoom.create(
                request.getTitle(),
                request.getType(),
                hash
        );

        chatRoomRepository.save(room);
        saveMembers(room.getId(), new ArrayList<>(members));

        return new ChatRoomResponse(room.getId(), request.getType());
    }

    private void saveMembers(Long roomId, List<Long> userIds) {
        List<ChatRoomMember> members = userIds.stream().map(userId ->
                ChatRoomMember.create(roomId, userId)
        ).toList();

        logger.info("========== members : {} ===========", members);

        chatRoomMemberRepository.saveAll(members);
    }

    public List<ChatRoomLastMessageDto> findParticipatedRooms(Long userId) {
        return chatRoomRepository.findAllChatRoomAndLastMessageByUserId(userId);
    }
}
