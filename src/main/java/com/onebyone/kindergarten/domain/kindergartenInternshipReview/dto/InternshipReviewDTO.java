package com.onebyone.kindergarten.domain.kindergartenInternshipReview.dto;

import com.onebyone.kindergarten.domain.kindergartenInternshipReview.entity.KindergartenInternshipReview;
import com.onebyone.kindergarten.domain.kindergatens.dto.KindergartenSimpleDTO;
import com.onebyone.kindergarten.domain.user.dto.SimpleUserDTO;
import lombok.Data;

@Data
public class InternshipReviewDTO {
    private Long internshipReviewId;
    private SimpleUserDTO user;
    private KindergartenSimpleDTO kindergarten;
    private String oneLineComment;
    private String workEnvironmentComment;
    private Integer workEnvironmentScore;
    private String learningSupportComment;
    private Integer learningSupportScore;
    private String instructionTeacherComment;
    private Integer instructionTeacherScore;
    private Integer likeCount;
    private Integer shareCount;

    public static InternshipReviewDTO fromEntity(KindergartenInternshipReview review) {
        InternshipReviewDTO dto = new InternshipReviewDTO();
        dto.setInternshipReviewId(review.getId());

        SimpleUserDTO userDTO = new SimpleUserDTO();
        userDTO.setUserId(review.getUser().getId());
        userDTO.setNickname(review.getUser().getNickname());
        dto.setUser(userDTO);

        KindergartenSimpleDTO kindergartenDTO = new KindergartenSimpleDTO();
        kindergartenDTO.setKindergartenId(review.getKindergarten().getId());
        kindergartenDTO.setName(review.getKindergarten().getName());
        dto.setKindergarten(kindergartenDTO);

        dto.setOneLineComment(review.getOneLineComment());
        dto.setWorkEnvironmentComment(review.getWorkEnvironmentComment());
        dto.setWorkEnvironmentScore(review.getWorkEnvironmentScore());
        dto.setLearningSupportComment(review.getLearningSupportComment());
        dto.setLearningSupportScore(review.getLearningSupportScore());
        dto.setInstructionTeacherComment(review.getInstructionTeacherComment());
        dto.setInstructionTeacherScore(review.getInstructionTeacherScore());
        dto.setLikeCount(review.getLikeCount());
        dto.setShareCount(review.getShareCount());

        return dto;
    }
}
