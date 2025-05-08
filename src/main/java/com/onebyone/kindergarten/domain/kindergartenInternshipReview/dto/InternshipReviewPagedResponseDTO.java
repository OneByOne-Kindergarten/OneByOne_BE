package com.onebyone.kindergarten.domain.kindergartenInternshipReview.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

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
