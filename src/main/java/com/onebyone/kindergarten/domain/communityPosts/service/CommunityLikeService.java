package com.onebyone.kindergarten.domain.communityPosts.service;

import com.onebyone.kindergarten.domain.communityPosts.dto.response.CommunityLikeResponseDTO;
import com.onebyone.kindergarten.domain.communityPosts.entity.CommunityLike;
import com.onebyone.kindergarten.domain.communityPosts.entity.CommunityPost;
import com.onebyone.kindergarten.domain.communityPosts.repository.CommunityLikeRepository;
import com.onebyone.kindergarten.domain.communityPosts.repository.CommunityRepository;
import com.onebyone.kindergarten.domain.pushNotification.service.NotificationTemplateService;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.domain.user.service.UserService;
import com.onebyone.kindergarten.global.exception.BusinessException;
import com.onebyone.kindergarten.global.exception.ErrorCodes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityLikeService {

  private final CommunityLikeRepository communityLikeRepository;
  private final CommunityRepository communityRepository;
  private final UserService userService;
  private final NotificationTemplateService notificationTemplateService;

  /// 게시글 좋아요 상태 조회 및 좋아요 수 조회
  public CommunityLikeResponseDTO getLikeInfo(Long postId, Long userId) {

    // 사용자 조회
    User user = userService.getUserById(userId);

    // 한 번의 쿼리로 좋아요 상태와 개수를 함께 조회
    return communityLikeRepository
        .findLikeInfo(postId, user)
        .orElse(new CommunityLikeResponseDTO(false, 0));
  }

  /// 게시글 좋아요/좋아요 취소 토글
  @Transactional
  public CommunityLikeResponseDTO toggleLike(Long postId, Long userId) {
    // 사용자 조회
    User user = userService.getUserById(userId);

    // 게시글 존재 여부와 좋아요 여부를 한 번에 확인
    return communityLikeRepository
        .findByUserAndPostId(user, postId)
        .map(
            like -> {
              // 좋아요 취소
              communityLikeRepository.delete(like);
              communityRepository.updateLikeCount(postId, -1);
              return new CommunityLikeResponseDTO(false, like.getPost().getLikeCount() - 1);
            })
        .orElseGet(
            () -> {
              // 게시글 존재 여부 확인 및 좋아요 추가
              CommunityPost post =
                  communityRepository
                      .findById(postId)
                      .orElseThrow(() -> new BusinessException(ErrorCodes.NOT_FOUND_POST));

              CommunityLike newLike = CommunityLike.builder().user(user).post(post).build();
              communityLikeRepository.save(newLike);
              communityRepository.updateLikeCount(postId, 1);

              // 알림 발송 - 본인 글이 아니고 삭제된 게시글이 아닌 경우
              if (!post.getUser().getId().equals(user.getId()) && post.getDeletedAt() == null) {
                notificationTemplateService.sendLikeNotification(
                    post.getUser().getId(), user, post.getTitle(), post.getId());
              }

              return new CommunityLikeResponseDTO(true, post.getLikeCount() + 1);
            });
  }
}
