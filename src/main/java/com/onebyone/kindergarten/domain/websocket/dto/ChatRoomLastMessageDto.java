package com.onebyone.kindergarten.domain.websocket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ChatRoomLastMessageDto {
    private Long chatRoomId;
    private String title;
    private String lastMessage;
    private Long senderId;
    private LocalDateTime lastMessageAt;

    public ChatRoomLastMessageDto(
        Long chatRoomId,
        String title,
        String lastMessage,
        Long senderId,
        LocalDateTime lastMessageAt
    ) {
        this.chatRoomId = chatRoomId;
        this.title = title;
        this.lastMessage = lastMessage;
        this.senderId = senderId;
        this.lastMessageAt = lastMessageAt;
    }
}