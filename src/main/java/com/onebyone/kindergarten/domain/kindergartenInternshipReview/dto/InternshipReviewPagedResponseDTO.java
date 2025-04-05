package com.onebyone.kindergarten.domain.kindergartenInternshipReview.dto;

import lombok.Data;

import java.util.List;

@Data
public class InternshipReviewPagedResponseDTO {
    private List<InternshipReviewDTO> content;
    private int totalPages;
}
