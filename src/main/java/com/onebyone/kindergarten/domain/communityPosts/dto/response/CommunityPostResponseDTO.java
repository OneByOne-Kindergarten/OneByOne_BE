package com.onebyone.kindergarten.domain.communityPosts.dto.response;

import com.onebyone.kindergarten.domain.communityPosts.enums.PostCategory;
import com.onebyone.kindergarten.domain.user.enums.UserRole;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommunityPostResponseDTO {
  private Long id;
  private String title;
  private String content;
  private PostCategory category;
  private String categoryName;
  private String categoryDescription;
  private String userNickname;
  private String userEmail;
  private UserRole userRole;
  private String career;
  private Integer viewCount;
  private Integer likeCount;
  private Integer commentCount;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
