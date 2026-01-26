package com.onebyone.kindergarten.domain.websocket.repository;

import com.onebyone.kindergarten.domain.websocket.domain.ChatRoom;
import com.onebyone.kindergarten.domain.websocket.dto.ChatRoomLastMessageDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
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


//    SELECT ch.id as chatRoomId,
//    ch.title as title,
//    crmes.message as lastMessage,
//    crmes.createdAt as lastMessageAt
//    FROM ChatRoom as ch
//    INNER JOIN ChatRoomMessage crmes ON
//    ch.id = (
//    SELECT chatRoomId
//    FROM ChatRoomMessage
//    where chatRoomId = ch.id
//    order by createdAt DESC
//    limit 1
//            )
//    WHERE ch.id IN (
//            SELECT crmem.chatRoomId
//                    FROM ChatRoomMember crmem
//                    where crmem.userId = :userId
//    )

//  채팅 메시지 innerjoin 추가
    @Query("""
       SELECT new com.onebyone.kindergarten.domain.websocket.dto.ChatRoomLastMessageDto
           (
                room.id,
                room.title,
                message.message,
                message.senderId,
                message.createdAt
           )
       FROM ChatRoom room
           JOIN ChatRoomMember member
               ON member.chatRoomId = room.id
           LEFT JOIN ChatRoomMessage message
               ON message.chatRoomId = room.id AND message.createdAt =
                   (
                        SELECT MAX(message2.createdAt)
                        FROM ChatRoomMessage message2
                        WHERE message2.chatRoomId = room.id
                   )
       WHERE member.userId = :userId
""")
    List<ChatRoomLastMessageDto> findAllChatRoomAndLastMessageByUserId(Long userId);
}
