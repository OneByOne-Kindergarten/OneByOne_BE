package com.onebyone.kindergarten.domain.kindergartenInternshipReview.dto;
import com.onebyone.kindergarten.domain.user.dto.SimpleUserDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InternshipReviewDTO {
    private Long internshipReviewId;
    private SimpleUserDTO user;
    private Long kindergartenId;
    private String kindergartenName;
    private String oneLineComment;
    private String workEnvironmentComment;
    private Integer workEnvironmentScore;
    private String learningSupportComment;
    private Integer learningSupportScore;
    private String instructionTeacherComment;
    private Integer instructionTeacherScore;
    private Integer likeCount;
    private Integer shareCount;
    private LocalDateTime createdAt;
    private String workType;

    // JPQL 쿼리를 위한 생성자 (유치원 ID와 이름 포함)
    public InternshipReviewDTO(
            Long internshipReviewId, Long userId, String nickname,
            Long kindergartenId, String kindergartenName, String oneLineComment, String workEnvironmentComment,
            Integer workEnvironmentScore, String learningSupportComment,
            Integer learningSupportScore, String instructionTeacherComment,
            Integer instructionTeacherScore, Integer likeCount,
            Integer shareCount, LocalDateTime createdAt
    ) {
        this.internshipReviewId = internshipReviewId;
        this.user = new SimpleUserDTO(userId, nickname);
        this.kindergartenId = kindergartenId;
        this.kindergartenName = kindergartenName;
        this.oneLineComment = oneLineComment;
        this.workEnvironmentComment = workEnvironmentComment;
        this.workEnvironmentScore = workEnvironmentScore;
        this.learningSupportComment = learningSupportComment;
        this.learningSupportScore = learningSupportScore;
        this.instructionTeacherComment = instructionTeacherComment;
        this.instructionTeacherScore = instructionTeacherScore;
        this.likeCount = likeCount;
        this.shareCount = shareCount;
        this.createdAt = createdAt;
    }
}
