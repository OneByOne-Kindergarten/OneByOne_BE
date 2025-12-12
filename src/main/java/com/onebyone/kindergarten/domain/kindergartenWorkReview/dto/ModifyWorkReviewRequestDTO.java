package com.onebyone.kindergarten.domain.kindergartenWorkReview.dto;

import lombok.Data;

@Data
public class ModifyWorkReviewRequestDTO {
  private Long kindergartenId;
  private String workType;
  private Long workReviewId;
  private String oneLineComment;
  private String benefitAndSalaryComment;
  private Integer benefitAndSalaryScore;
  private String workLifeBalanceComment;
  private Integer workLifeBalanceScore;
  private String workEnvironmentComment;
  private Integer workEnvironmentScore;
  private String managerComment;
  private Integer managerScore;
  private String customerComment;
  private Integer customerScore;
}
