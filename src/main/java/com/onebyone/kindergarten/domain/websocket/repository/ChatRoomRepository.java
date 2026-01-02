package com.onebyone.kindergarten.domain.websocket.repository;

import com.onebyone.kindergarten.domain.websocket.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

//    @Query("""
//        SELECT crm1.chatRoomId
//        FROM ChatRoomMember crm1
//            JOIN ChatRoomMember crm2
//                on crm1.chatRoomId = crm2.chatRoomId
//        JOIN ChatRoom cr
//            on cr.id = crm1.id
//        WHERE cr.type = 'PRIVATE'
//            AND crm1.userId = :userId
//            AND crm2.userId = :targetUserId
//    """)
//    Optional<Long> findPrivateRoom(Long userId, Long targetUserId);

    Optional<ChatRoom> findByMemberHash(String memberHash);
}
