package com.onebyone.kindergarten.domain.websocket.service;

import com.onebyone.kindergarten.domain.websocket.domain.ChatRoom;
import com.onebyone.kindergarten.domain.websocket.domain.ChatRoomMember;
import com.onebyone.kindergarten.domain.websocket.dto.ChatRoomCreateRequest;
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


//    private ChatRoomResponse createPrivateRoom(Long userId, ChatRoomCreateRequest request) {
//
//        if (request.getUserIds().size() != 1) {
//            throw new BusinessException(ErrorCodes.INVALID_CHAT_ROOM_MEMBER);
//        }
//
//        Long targetUserId = request.getUserIds().get(0);
//
//        // 기존 1:1 방 조회
//        Optional<Long> existingRoomId =
//                chatRoomRepository.findPrivateRoom(userId, targetUserId);
//
//        if (existingRoomId.isPresent()) {
//            logger.info("========== 기존 방 존재 ==========");
//            return new ChatRoomResponse(
//                    existingRoomId.get(),
//                    RoomType.PRIVATE
//            );
//        }
//
//        logger.info("========== 개인 방 생성 시작 ==========");
//        ChatRoom room = ChatRoom.create(request.getTitle(), RoomType.PRIVATE);
//        chatRoomRepository.save(room);
//        logger.info("========== 개인 방 생성 종료 ==========");
//
//        logger.info("========== 개인 방 멤버 생성 시작 ==========");
//        saveMembers(room.getId(), List.of(userId, targetUserId));
//        logger.info("========== 개인 방 멤버 생성 종료 ==========");
//
//        return new ChatRoomResponse(room.getId(), RoomType.PRIVATE);
//    }

//    private ChatRoomResponse createGroupRoom(Long userId, ChatRoomCreateRequest request) {
//
//        if (request.getUserIds().size() < 2) {
//            throw new BusinessException(ErrorCodes.INVALID_CHAT_ROOM_MEMBER);
//        }
//
//        if (request.getTitle() == null || request.getTitle().isBlank()) {
//            throw new BusinessException(ErrorCodes.INVALID_CHAT_ROOM_TITLE);
//        }
//
//        logger.info("========== 단체 방 생성 시작 ==========");
//        ChatRoom room = ChatRoom.create(request.getTitle(), RoomType.GROUP);
//        chatRoomRepository.save(room);
//        logger.info("========== 단체 방 생성 종료 ==========");
//
//        List<Long> members = new ArrayList<>();
//        members.add(userId);
//        members.addAll(request.getUserIds());
//
//        logger.info("========== 단체 방 멤버 생성 시작 ==========");
//        saveMembers(room.getId(), members);
//        logger.info("========== 단체 방 멤버 생성 종료 ==========");
//
//        return new ChatRoomResponse(room.getId(), RoomType.GROUP);
//    }

    private ChatRoomResponse createRoomInternal(
            Long userId,
            ChatRoomCreateRequest request
    ) {
        Set<Long> members = new HashSet<>();
        members.add(userId);
        members.addAll(request.getUserIds());

        String hash = ChatRoomHashUtil.generate(members);

        Optional<ChatRoom> existing =
                chatRoomRepository.findByMemberHash(hash);

        if (existing.isPresent()) {
            logger.info("========== 기존에 존재하는 대화방 ==========");
            return new ChatRoomResponse(
                    existing.get().getId(),
                    existing.get().getType()
            );
        }

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
}
