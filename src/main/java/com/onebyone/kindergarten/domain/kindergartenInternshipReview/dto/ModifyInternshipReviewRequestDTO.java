package com.onebyone.kindergarten.domain.kindergartenInternshipReview.dto;

import lombok.Data;

@Data
public class ModifyInternshipReviewRequestDTO {
    private Long kindergartenId;
    private String workType;
    private Long internshipReviewId;
    private String oneLineComment;
    private String instructionTeacherComment;
    private Integer instructionTeacherScore;
    private String learningSupportComment;
    private Integer learningSupportScore;
    private String workEnvironmentComment;
    private Integer workEnvironmentScore;
}
