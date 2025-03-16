package com.onebyone.kindergarten.domain.reports.entity;

import com.onebyone.kindergarten.domain.reports.enums.ReportTargetType;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.global.common.BaseEntity;
import com.onebyone.kindergarten.global.enums.ReportStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Report extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 신고 코드

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter; // 신고자

    @Column(nullable = false)
    private Long targetId; // 신고 대상 코드

    @Enumerated(EnumType.STRING)
    private ReportTargetType targetType; // 신고 대상 타입 - 리뷰, 게시글, 댓글

    @Column(nullable = false)
    private String reason; // 신고 사유

    @Enumerated(EnumType.STRING)
    private ReportStatus status; // 신고 상태 - 처리중, 처리완료, 거절
}
