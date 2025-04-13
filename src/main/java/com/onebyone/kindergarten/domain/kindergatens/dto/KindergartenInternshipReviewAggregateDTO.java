package com.onebyone.kindergarten.domain.kindergatens.dto;

import com.onebyone.kindergarten.domain.kindergatens.entity.KindergartenInternshipReviewAggregate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KindergartenInternshipReviewAggregateDTO {

    private BigDecimal workEnvironmentScoreAggregate;
    private BigDecimal learningSupportScoreAggregate;
    private BigDecimal instructionTeacherScoreAggregate;

    public static KindergartenInternshipReviewAggregateDTO from(KindergartenInternshipReviewAggregate entity) {
        return KindergartenInternshipReviewAggregateDTO.builder()
                .workEnvironmentScoreAggregate(entity.getWorkEnvironmentScoreAggregate())
                .learningSupportScoreAggregate(entity.getLearningSupportScoreAggregate())
                .instructionTeacherScoreAggregate(entity.getInstructionTeacherScoreAggregate())
                .build();
    }
}
