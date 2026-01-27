package com.onebyone.kindergarten.domain.communityPosts.mapper;

import com.onebyone.kindergarten.domain.communityPosts.dto.request.CreateCommunityPostRequestDTO;
import com.onebyone.kindergarten.domain.communityPosts.dto.response.CommunityPostResponseDTO;
import com.onebyone.kindergarten.domain.communityPosts.entity.CommunityCategory;
import com.onebyone.kindergarten.domain.communityPosts.entity.CommunityPost;
import com.onebyone.kindergarten.domain.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class CommunityPostMapper {

  /// 엔티티 변환
  public CommunityPost toEntity(
      CreateCommunityPostRequestDTO request, User user, CommunityCategory category) {
    return CommunityPost.builder()
        .title(request.getTitle())
        .content(request.getContent())
        .category(request.getCategory())
        .communityCategory(category)
        .user(user)
        .build();
  }

  /// 응답 DTO 변환
  public CommunityPostResponseDTO toResponse(CommunityPost post) {
    return CommunityPostResponseDTO.builder()
        .id(post.getId())
        .title(post.getTitle())
        .content(post.getContent())
        .category(post.getCategory())
        .categoryName(post.getCommunityCategory().getCategoryName())
        .categoryDescription(post.getCommunityCategory().getDescription())
        .userNickname(post.getUser().getNickname())
        .userEmail(post.getUser().getEmail())
        .userRole(post.getUser().getRole())
        .career(post.getUser().getCareer())
        .hasWrittenReview(post.getUser().hasWrittenReview())
        .viewCount(post.getViewCount())
        .likeCount(post.getLikeCount())
        .commentCount(post.getCommentCount())
        .createdAt(post.getCreatedAt())
        .updatedAt(post.getUpdatedAt())
        .build();
  }
}
