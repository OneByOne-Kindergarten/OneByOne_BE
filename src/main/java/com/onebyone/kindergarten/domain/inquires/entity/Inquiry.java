package com.onebyone.kindergarten.domain.inquires.entity;

import com.onebyone.kindergarten.domain.inquires.enums.InquiryStatus;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
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

    @Column(length = 1000)
    private String answer; // 답변

    @Enumerated(EnumType.STRING)
    private InquiryStatus status = InquiryStatus.PENDING; // 문의 상태 - 처리중, 답변완료, 답변대기

    @Builder
    public Inquiry(User user, String title, String content) {
        this.user = user;
        this.title = title;
        this.content = content;
    }

    // 답변 등록
    public void answerInquiry(String answer) {
        this.answer = answer;
        this.status = InquiryStatus.ANSWERED;
    }

    // 문의 마감
    public void closeInquiry() {
        this.status = InquiryStatus.CLOSED;
    }
}
