package com.onebyone.kindergarten.domain.communityPosts.dto.request;

import com.onebyone.kindergarten.domain.communityPosts.enums.PostCategory;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommunitySearchDTO {
  private String title; // 제목
  private String content; // 내용
  private PostCategory category; // 카테고리(enum)
  private String categoryName; // 카테고리 이름
  private String userName; // 작성자 이름
  private LocalDateTime startDate; // 검색 시작일
  private LocalDateTime endDate; // 검색 종료일
}
