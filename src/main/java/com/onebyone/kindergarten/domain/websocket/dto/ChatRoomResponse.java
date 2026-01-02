package com.onebyone.kindergarten.domain.websocket.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class ChatRoomResponse {
    private Long roomId;
    private RoomType type;

    @Builder
    public ChatRoomResponse(Long roomId, RoomType type) {
        this.roomId = roomId;
        this.type = type;
    }
}