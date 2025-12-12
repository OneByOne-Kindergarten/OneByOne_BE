package com.onebyone.kindergarten.domain.communityPosts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommunityLikeResponseDTO {
  private boolean liked; // 좋아요 상태
  private int likeCount; // 좋아요 수
}
