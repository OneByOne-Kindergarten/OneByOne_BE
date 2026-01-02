package com.onebyone.kindergarten.domain.websocket.repository;

import com.onebyone.kindergarten.domain.websocket.domain.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {
}
