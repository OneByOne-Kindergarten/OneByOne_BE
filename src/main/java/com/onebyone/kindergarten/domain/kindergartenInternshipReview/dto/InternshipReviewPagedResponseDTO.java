package com.onebyone.kindergarten.domain.kindergartenInternshipReview.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InternshipReviewPagedResponseDTO {
  private List<InternshipReviewDTO> content;
  private int totalPages;

  public enum SortType {
    LATEST, // 최신순
    POPULAR // 좋아요순
  }
}
