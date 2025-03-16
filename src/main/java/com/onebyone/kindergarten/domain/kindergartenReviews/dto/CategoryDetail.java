package com.onebyone.kindergarten.domain.kindergartenReviews.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDetail {
    private Double score;      // 카테고리별 점수 (0.0 ~ 5.0)
    private String content;    // 카테고리 제목/이름
    private String description; // 카테고리별 상세 설명
} 