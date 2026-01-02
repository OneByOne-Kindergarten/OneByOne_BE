package com.onebyone.kindergarten.domain.websocket.controller;

import com.onebyone.kindergarten.domain.websocket.dto.ChatRoomCreateRequest;
import com.onebyone.kindergarten.domain.websocket.dto.ChatRoomResponse;
import com.onebyone.kindergarten.domain.websocket.service.ChatRoomService;
import com.onebyone.kindergarten.global.facade.ChatRoomFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChatRoomApiController {
    private final ChatRoomFacade chatRoomFacade;
    private final ChatRoomService chatRoomService;

    /** 테스트 시나리오
     * 1. 채팅방 개인 생성 - O
     * 2. 채팅방 그룹 생성 - O
     * 3. 개인 채팅방 대상이 자기 자신일 경우 - O
     * 4. 단체 채팅방에 자기 자신이 포함될 경우 - O
     * 5. 개인 채팅방 대상의 상태가 유효하지 않을 때 - O
     * 6. 단체 채팅방 대상중의 상태가 유효하지 않는 유저가 포함될 경우 - O
     * 7. 이미 대상과의 개인 채팅방이 존재할경우 - O
     * 8. 이미 대상들과의 단체 채팅방이 존재할경우 - O
     * 9. 개인 채팅방의 제목이 없는 경우 - 이건 제목 어쩌지
     * 10. 단체 채팅방의 제목이 없는 경우 - O
     * 11. 이미 대상과의 개인 채팅방이 존재하지만 대상이 활성화 상태가 아닐 때
     * 12. 이미 대상들과의 단체 채팅방이 존재하지만 대상중에 활성화 상태가 아닐 때
     * 13. 개인 채팅방인데 대상이 2명 이상일 때 - 에러 발생
     * 14. 단체 채팅방에서 중복된 유저가 있을 때
     * 15. 단체 채팅방에서 대상이 1명 뿐일 때
     * 16. memberIds가 비어있거나 없을 때
     * 17. 존재하지 않는 userId 일 때
     * 18. 차단, 신고 유저는 방 못 만들게 해야함
     */

    @PostMapping("/chat/rooms")
    public ChatRoomResponse createRooms(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ChatRoomCreateRequest request
    ) {
        Long userId = Long.parseLong(userDetails.getUsername());
        return chatRoomFacade.createRoom(userId, request);
    }

}
