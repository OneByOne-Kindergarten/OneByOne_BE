package com.onebyone.kindergarten.domain.communityComments.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.onebyone.kindergarten.domain.communityComments.repository.CommunityCommentRepository;
import com.onebyone.kindergarten.domain.communityComments.dto.request.CreateCommentRequestDTO;
import com.onebyone.kindergarten.domain.communityComments.dto.response.CommentResponseDTO;
import com.onebyone.kindergarten.domain.communityComments.entity.CommunityComment;
import com.onebyone.kindergarten.domain.communityPosts.entity.CommunityPost;
import com.onebyone.kindergarten.domain.communityPosts.repository.CommunityRepository;
import com.onebyone.kindergarten.domain.pushNotification.enums.NotificationType;
import com.onebyone.kindergarten.domain.pushNotification.event.PushNotificationEventPublisher;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.domain.user.service.UserService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityCommentService {

    private final CommunityCommentRepository commentRepository;
    private final CommunityRepository postRepository;
    private final UserService userService;
    private final PushNotificationEventPublisher notificationEventPublisher;

    /// 댓글 작성
    @Transactional
    public CommentResponseDTO createComment(Long postId, CreateCommentRequestDTO dto, String email) {

        // 사용자 조회
        User user = userService.getUserByEmail(email);
        
        // 게시글 조회 (작성자 정보를 포함)
        CommunityPost post = postRepository.findByIdWithUser(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 댓글 작성
        CommunityComment comment = CommunityComment.builder()
                .post(post)
                .user(user)
                .content(dto.getContent())
                .build();
        commentRepository.save(comment);

        // 댓글 수 업데이트
        postRepository.incrementCommentCount(postId);

        /// TODO : 타입별 공통 형태 메서드 구현 필요
        if (!post.getUser().getId().equals(user.getId())) {
            // 푸시 알림 이벤트 발행
            notificationEventPublisher.publish(
                    post.getUser().getId(),
                    user.getNickname() + "님이 댓글을 남겼습니다",
                    dto.getContent(),
                    NotificationType.COMMENT,
                    postId
            );
        }
        
        return CommentResponseDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .nickName(user.getNickname())
                .career(user.getCareer())
                .userRole(user.getRole())
                .createdAt(comment.getCreatedAt())
                .status(comment.getStatus())
                .build();
    }

    /// 게시글의 댓글 목록 조회
    public Page<CommentResponseDTO> getComments(Long postId, Pageable pageable) {
        return commentRepository.findCommentDTOsByPostId(postId, pageable);
    }
}
