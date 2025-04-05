package com.onebyone.kindergarten.domain.kindergartenWorkReview.dto;

import com.onebyone.kindergarten.domain.kindergartenWorkReview.entity.KindergartenWorkReview;
import com.onebyone.kindergarten.domain.user.dto.SimpleUserDTO;
import lombok.Data;

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

    public static WorkReviewDTO fromEntity(KindergartenWorkReview review) {
        WorkReviewDTO dto = new WorkReviewDTO();
        dto.setWorkReviewId(review.getId());

        SimpleUserDTO userDTO = new SimpleUserDTO();
        userDTO.setUserId(review.getUser().getId());
        userDTO.setNickname(review.getUser().getNickname());
        dto.setUser(userDTO);

        dto.setWorkYear(review.getWorkYear());
        dto.setOneLineComment(review.getOneLineComment());
        dto.setBenefitAndSalaryComment(review.getBenefitAndSalaryComment());
        dto.setBenefitAndSalaryScore(review.getBenefitAndSalaryScore());
        dto.setWorkLifeBalanceComment(review.getWorkLifeBalanceComment());
        dto.setWorkLifeBalanceScore(review.getWorkLifeBalanceScore());
        dto.setWorkEnvironmentComment(review.getWorkEnvironmentComment());
        dto.setWorkEnvironmentScore(review.getWorkEnvironmentScore());
        dto.setManagerComment(review.getManagerComment());
        dto.setManagerScore(review.getManagerScore());
        dto.setCustomerComment(review.getCustomerComment());
        dto.setCustomerScore(review.getCustomerScore());
        dto.setLikeCount(review.getLikeCount());
        dto.setShareCount(review.getShareCount());

        return dto;
    }
}
