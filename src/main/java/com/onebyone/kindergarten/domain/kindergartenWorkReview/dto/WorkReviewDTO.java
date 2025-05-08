package com.onebyone.kindergarten.domain.kindergartenWorkReview.dto;

import com.onebyone.kindergarten.domain.user.dto.SimpleUserDTO;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class WorkReviewDTO {
    private Long workReviewId;
    private SimpleUserDTO user;
    private Integer workYear;
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
    private Integer likeCount;
    private Integer shareCount;
    private LocalDateTime createdAt;
    private String workType;

    // JPQL 쿼리를 위한 생성자
    public WorkReviewDTO(
        Long workReviewId, Long userId, String nickname,
        Integer workYear, String oneLineComment,
        String benefitAndSalaryComment, Integer benefitAndSalaryScore,
        String workLifeBalanceComment, Integer workLifeBalanceScore,
        String workEnvironmentComment, Integer workEnvironmentScore,
        String managerComment, Integer managerScore,
        String customerComment, Integer customerScore,
        Integer likeCount, Integer shareCount,
        LocalDateTime createdAt, String workType
    ) {
        this.workReviewId = workReviewId;
        this.user = new SimpleUserDTO(userId, nickname);
        this.workYear = workYear;
        this.oneLineComment = oneLineComment;
        this.benefitAndSalaryComment = benefitAndSalaryComment;
        this.benefitAndSalaryScore = benefitAndSalaryScore;
        this.workLifeBalanceComment = workLifeBalanceComment;
        this.workLifeBalanceScore = workLifeBalanceScore;
        this.workEnvironmentComment = workEnvironmentComment;
        this.workEnvironmentScore = workEnvironmentScore;
        this.managerComment = managerComment;
        this.managerScore = managerScore;
        this.customerComment = customerComment;
        this.customerScore = customerScore;
        this.likeCount = likeCount;
        this.shareCount = shareCount;
        this.createdAt = createdAt;
        this.workType = workType;
    }
}
