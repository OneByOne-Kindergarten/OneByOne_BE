package com.onebyone.kindergarten.domain.communityPosts.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PopularPostsResponseDTO {
  private List<CommunityPostResponseDTO> weekly; // 주간 인기 게시글
  private List<CommunityPostResponseDTO> monthly; // 월간 인기 게시글
  private List<CommunityPostResponseDTO> all; // 전체 인기 게시글
}
