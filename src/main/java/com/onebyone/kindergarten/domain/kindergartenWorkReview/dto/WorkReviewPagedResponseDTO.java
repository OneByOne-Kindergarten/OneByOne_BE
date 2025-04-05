package com.onebyone.kindergarten.domain.kindergartenWorkReview.dto;

import lombok.Data;

import java.util.List;

@Data
public class WorkReviewPagedResponseDTO {
    private List<WorkReviewDTO> content;
    private int totalPages;
}
