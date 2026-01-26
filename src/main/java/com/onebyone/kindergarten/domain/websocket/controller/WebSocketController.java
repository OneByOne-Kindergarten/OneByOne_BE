package com.onebyone.kindergarten.domain.websocket.controller;

import com.onebyone.kindergarten.domain.websocket.dto.ChatMessageDto;
import com.onebyone.kindergarten.global.facade.ChatRoomFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Slf4j
@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class WebSocketController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRoomFacade chatRoomFacade;

    @MessageMapping("/chat.send/{chatRoomId}")
    public void saveChat(
            @DestinationVariable Long chatRoomId,
            @Payload String message,
            Principal principal
    ) {
        Authentication auth = (Authentication) principal;
        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        Long userId = Long.parseLong(userDetails.getUsername());

        log.info("roomId={}, senderId={}, message={}",
                chatRoomId, userId, message);

        chatRoomFacade.saveChat(chatRoomId, userId, message);
    }
}
