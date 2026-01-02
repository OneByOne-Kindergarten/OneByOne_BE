package com.onebyone.kindergarten.domain.websocket.domain;

import com.onebyone.kindergarten.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
    name = "chat_room_member"
)
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long chatRoomId;
    private Long userId;

    public static ChatRoomMember create(Long chatRoomId, Long userId) {
        return new ChatRoomMember(null, chatRoomId, userId);
    }
}
