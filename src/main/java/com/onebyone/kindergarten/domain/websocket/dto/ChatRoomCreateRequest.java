package com.onebyone.kindergarten.domain.websocket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ChatRoomCreateRequest {
    private RoomType type;
    private String title;
    private List<Long> userIds;
}
