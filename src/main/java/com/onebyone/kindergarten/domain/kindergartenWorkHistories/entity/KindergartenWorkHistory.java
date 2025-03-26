package com.onebyone.kindergarten.domain.kindergartenWorkHistories.entity;

import com.onebyone.kindergarten.global.common.BaseEntity;
import com.onebyone.kindergarten.global.enums.ReviewType;
import com.onebyone.kindergarten.domain.kindergatens.entity.Kindergarten;
import com.onebyone.kindergarten.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity(name = "kindergarten_work_history")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KindergartenWorkHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 이력 코드

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 유저

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kindergarten_id", nullable = false)
    private Kindergarten kindergarten; // 유치원

    @Column(name = "start_date")
    private LocalDate startDate; // 시작일

    @Column(name = "end_date")
    private LocalDate endDate; // 종료일

    @Enumerated(EnumType.STRING)
    private ReviewType workType; // 근무/실습 타입 - 근무, 실습
}