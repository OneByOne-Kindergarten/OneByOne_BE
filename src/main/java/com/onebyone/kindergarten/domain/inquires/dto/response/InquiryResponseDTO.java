package com.onebyone.kindergarten.domain.inquires.dto.response;

import com.onebyone.kindergarten.domain.inquires.entity.Inquiry;
import com.onebyone.kindergarten.domain.inquires.enums.InquiryStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class InquiryResponseDTO {
    private Long id;
    private String title;
    private String content;
    private String answer;
    private InquiryStatus status;
    private String userName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static InquiryResponseDTO fromEntity(Inquiry inquiry) {
        return InquiryResponseDTO.builder()
                .id(inquiry.getId())
                .title(inquiry.getTitle())
                .content(inquiry.getContent())
                .answer(inquiry.getAnswer())
                .status(inquiry.getStatus())
                .userName(inquiry.getUser().getNickname())
                .createdAt(inquiry.getCreatedAt())
                .updatedAt(inquiry.getUpdatedAt())
                .build();
    }
} 