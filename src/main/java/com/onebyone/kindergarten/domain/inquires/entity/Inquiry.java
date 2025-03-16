package com.onebyone.kindergarten.domain.inquires.entity;

import com.onebyone.kindergarten.domain.inquires.enums.InquiryStatus;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.global.common.BaseEntity;
import jakarta.persistence.*;
@Entity
public class Inquiry extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 문의 코드

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 문의자

    @Column(nullable = false)
    private String title; // 제목

    @Column(nullable = false, length = 1000)
    private String content; // 내용

    @Enumerated(EnumType.STRING)
    private InquiryStatus status; // 문의 상태 - 처리중, 답변완료, 답변대기
}
