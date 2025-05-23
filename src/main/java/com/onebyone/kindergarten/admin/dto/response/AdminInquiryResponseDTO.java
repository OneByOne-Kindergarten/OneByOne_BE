package com.onebyone.kindergarten.admin.dto.response;

import com.onebyone.kindergarten.domain.inquires.entity.Inquiry;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AdminInquiryResponseDTO {
    private Long id;
    private String title;
    private String userName;
    private String content;
    private String answer;
    private String status;
    private LocalDateTime createdAt;

    public static AdminInquiryResponseDTO from(Inquiry inquiry) {
        return AdminInquiryResponseDTO.builder()
                .id(inquiry.getId())
                .title(inquiry.getTitle())
                .userName(inquiry.getUser().getNickname())
                .content(inquiry.getContent())
                .answer(inquiry.getAnswer())
                .status(inquiry.getStatus().name())
                .createdAt(inquiry.getCreatedAt())
                .build();
    }
} 