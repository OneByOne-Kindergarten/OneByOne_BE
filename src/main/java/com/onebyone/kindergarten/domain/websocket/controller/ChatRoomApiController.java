package com.onebyone.kindergarten.domain.websocket.controller;

import com.onebyone.kindergarten.domain.websocket.dto.ChatRoomCreateRequest;
import com.onebyone.kindergarten.domain.websocket.dto.ChatRoomLastMessageDto;
import com.onebyone.kindergarten.domain.websocket.dto.ChatRoomResponse;
import com.onebyone.kindergarten.global.facade.ChatRoomFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChatRoomApiController {
    private final ChatRoomFacade chatRoomFacade;

    /** 테스트 시나리오
     * 1. 채팅방 개인 생성 - O
     * 2. 채팅방 그룹 생성 - O
     * 3. 개인 채팅방 대상이 자기 자신일 경우 - O
     * 4. 단체 채팅방에 자기 자신이 포함될 경우 - O
     * 5. 개인 채팅방 대상의 상태가 유효하지 않을 때 - O
     * 6. 단체 채팅방 대상중의 상태가 유효하지 않는 유저가 포함될 경우 - O
     * 7. 이미 대상과의 개인 채팅방이 존재할경우 - O
     * 8. 이미 대상들과의 단체 채팅방이 존재할경우 - O
     * 9. 개인 채팅방의 제목이 없는 경우 - O
     * 10. 단체 채팅방의 제목이 없는 경우 - O
     * 11. 이미 대상과의 개인 채팅방이 존재하지만 대상이 활성화 상태가 아닌 유저가 있을 때 - 기존 방 return
     * 12. 이미 대상들과의 단체 채팅방이 존재하지만 대상중에 활성화 상태가 아닌 유저가 아닐 때 - 기존 방 return
     * 13. 개인 채팅방인데 대상이 2명 이상일 때 - O
     * 14. 단체 채팅방에서 중복된 유저가 있을 때 - O
     * 15. 단체 채팅방에서 대상이 1명 뿐일 때 - O
     * 16. memberIds가 비어있거나 없을 때 - O
     * 17. 존재하지 않는 userId 일 때 - O
     */

    @PostMapping("/chat/room")
    public ChatRoomResponse createRooms(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ChatRoomCreateRequest request
    ) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return chatRoomFacade.createRoom(userId, request);
    }

    /**
     * 1. 리스트 받아서 채팅방 소켓 연결
     * 2. 메시지 send 및 연결된 사용자 잘 오는지
     * 3. 소켓 연결된 상태에서 알림이 가는지 안 가는지
     * 4. 방 나갔을 때 초기화
     */

    @GetMapping("/chat/rooms")
    public List<ChatRoomLastMessageDto> getRooms(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return chatRoomFacade.findParticipatedRooms(userId);
    }

}
