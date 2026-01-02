package com.onebyone.kindergarten.domain.websocket.domain;

import com.onebyone.kindergarten.domain.websocket.dto.RoomType;
import com.onebyone.kindergarten.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "chat_room")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Enumerated(EnumType.STRING)
    private RoomType type;

    @Column(name = "member_hash", nullable = false, length = 64)
    private String memberHash;

    public static ChatRoom create(String title, RoomType type, String memberHash) {
        ChatRoom room = new ChatRoom();
        room.title = title;
        room.type = type;
        room.memberHash = memberHash;
        return room;
    }
}
