package com.onebyone.kindergarten.domain.websocket.domain;

import com.onebyone.kindergarten.global.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "chat_room_message")
public class ChatRoomMessage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_room_id")
    private Long chatRoomId;

    private String message;

    @Column(name = "sender_id")
    private Long senderId;

    public static create() {
        return new ChatRoomMessage();
    }
}
