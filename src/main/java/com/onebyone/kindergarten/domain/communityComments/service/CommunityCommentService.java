package com.onebyone.kindergarten.domain.communityComments.service;

import com.onebyone.kindergarten.domain.communityComments.dto.response.PageCommunityCommentsResponseDTO;
import com.onebyone.kindergarten.domain.user.enums.UserRole;
import com.onebyone.kindergarten.domain.userBlock.repository.UserBlockRepository;
import com.onebyone.kindergarten.global.exception.BusinessException;
import com.onebyone.kindergarten.global.exception.ErrorCodes;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.onebyone.kindergarten.domain.communityComments.repository.CommunityCommentRepository;
import com.onebyone.kindergarten.domain.communityComments.dto.request.CreateCommentRequestDTO;
import com.onebyone.kindergarten.domain.communityComments.dto.response.CommentResponseDTO;
import com.onebyone.kindergarten.domain.communityComments.entity.CommunityComment;
import com.onebyone.kindergarten.domain.communityPosts.entity.CommunityPost;
import com.onebyone.kindergarten.domain.communityPosts.repository.CommunityRepository;
import com.onebyone.kindergarten.domain.pushNotification.service.NotificationTemplateService;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.domain.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityCommentService {

    private final CommunityCommentRepository commentRepository;
    private final CommunityRepository postRepository;
    private final UserService userService;
    private final UserBlockRepository userBlockRepository;
    private final NotificationTemplateService notificationTemplateService;

    /// 댓글 작성 (원댓글 또는 대댓글)
    @Transactional
    public CommentResponseDTO createComment(Long postId, CreateCommentRequestDTO dto, Long userId) {

        // 사용자 조회
        User user = userService.getUserById(userId);
        
        // 게시글 조회 (작성자 정보를 포함)
        CommunityPost post = postRepository.findByIdWithUser(postId)
                .orElseThrow(() -> new BusinessException(ErrorCodes.NOT_FOUND_POST));

        CommunityComment parent = null;
        
        // 대댓글인 경우 부모 댓글 조회
        if (dto.getParentId() != null) {
            parent = commentRepository.findByIdWithUser(dto.getParentId())
                    .orElseThrow(() -> new BusinessException(ErrorCodes.NOT_FOUND_PARENT_COMMENT));
            
            // 부모 댓글의 게시글과 요청된 게시글이 일치하는지 확인
            if (!parent.getPost().getId().equals(postId)) {
                throw new BusinessException(ErrorCodes.PARENT_POST_MISMATCH);
            }
            
            // 이미 대댓글인 경우 대댓글에 대댓글 작성 방지
            if (parent.isReply()) {
                throw new BusinessException(ErrorCodes.REPLY_TO_REPLY_NOT_ALLOWED);
            }
            
            // 삭제된 댓글에는 대댓글 작성 불가
            if (parent.getDeletedAt() != null) {
                throw new BusinessException(ErrorCodes.REPLY_TO_DELETED_COMMENT_NOT_ALLOWED);
            }
        }

        // 댓글 작성
        CommunityComment comment = CommunityComment.builder()
                .post(post)
                .user(user)
                .content(dto.getContent())
                .parent(parent)
                .build();
        commentRepository.save(comment);

        // 댓글 수 업데이트 (최상위 댓글인 경우에만)
        if (parent == null) {
            postRepository.incrementCommentCount(postId);
        }

        // 알림 대상자 결정 (게시글 작성자 또는 부모 댓글 작성자)
        User notificationTarget = parent != null ? parent.getUser() : post.getUser();

        // 알림 발송 - 게시글이 삭제되지 않은 경우에만
        if (post.getDeletedAt() == null) {
            notificationTemplateService.sendCommentNotification(
                    notificationTarget.getId(), 
                    user, 
                    dto.getContent(), 
                    parent != null, 
                    postId
            );
        }
        
        return CommentResponseDTO.fromEntity(comment);
    }

    /// 게시글의 최상위 댓글 목록 조회 (대댓글 제외)
    public Page<CommentResponseDTO> getOriginalComments(Long postId, Pageable pageable) {
        return commentRepository.findOriginalCommentsByPostId(postId, pageable);
    }
    
    /// 특정 댓글의 대댓글 목록 조회
    public List<CommentResponseDTO> getReplies(Long commentId) {
        return commentRepository.findRepliesByParentId(commentId);
    }
    
    /// 게시글의 모든 댓글과 대댓글 목록 조회 (계층 구조로 정렬)
    public Page<CommentResponseDTO> getAllCommentsWithReplies(Long postId, Pageable pageable, Long userId) {

        // 차단된 사용자 ID 목록 조회
        List<Long> blockedUserIds = Collections.emptyList();
        if (userId != null) {
            User user = userService.getUserById(userId);
            blockedUserIds = userBlockRepository.findBlockedUserIdsByUserId(user.getId());
            if (blockedUserIds == null) {
                blockedUserIds = Collections.emptyList();
            }
        }
        
        return commentRepository.findAllCommentsWithRepliesByPostId(postId, blockedUserIds, pageable);
    }

    @Transactional(readOnly = true)
    public PageCommunityCommentsResponseDTO getWroteMyCommunityComments(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<CommentResponseDTO> commentsPage = commentRepository.findByUserId(userId, pageable)
                .map(CommentResponseDTO::fromEntity);

        PageCommunityCommentsResponseDTO response = new PageCommunityCommentsResponseDTO();
        response.setContent(commentsPage.getContent());
        response.setTotalPages(commentsPage.getTotalPages());

        return response;
    }

    /// 댓글 삭제 (소프트 삭제)
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        // 댓글 조회 (작성자 정보 포함)
        CommunityComment comment = commentRepository.findByIdWithUser(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCodes.NOT_FOUND_COMMENT));
        
        // 현재 사용자 조회
        User currentUser = userService.getUserById(userId);
        
        // 작성자 또는 관리자 권한 확인
        if (!comment.getUser().getId().equals(userId) && !currentUser.getRole().equals(UserRole.ADMIN)) {
            throw new BusinessException(ErrorCodes.UNAUTHORIZED_DELETE);
        }

        Long postId = comment.getPost().getId();
        boolean isOriginalComment = comment.getParent() == null;
        
        // 댓글 소프트 삭제 (deletedAt 설정)
        comment.markAsDeleted();
        
        // 대댓글이 있는 원댓글인 경우 대댓글들도 함께 삭제
        if (isOriginalComment) {
            commentRepository.updateRepliesDeletedAt(commentId, LocalDateTime.now());
        }
        
        // 원댓글이었다면 게시글의 댓글 수 감소
        if (isOriginalComment) {
            postRepository.decrementCommentCount(postId, 1);
        }
    }
}
