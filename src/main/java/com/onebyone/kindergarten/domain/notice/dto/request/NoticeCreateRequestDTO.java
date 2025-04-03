package com.onebyone.kindergarten.domain.notice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NoticeCreateRequestDTO {
    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 100, message = "제목은 100자를 초과할 수 없습니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    @Size(max = 2000, message = "내용은 2000자를 초과할 수 없습니다.")
    private String content;

    @Builder.Default
    private Boolean isPushSend = false;
    
    @Builder.Default
    private Boolean isPublic = true;

    // JPQL 생성자
    public NoticeCreateRequestDTO() {
        this.isPushSend = false;
        this.isPublic = true;
    }

    @Builder
    public NoticeCreateRequestDTO(String title, String content, Boolean isPushSend, Boolean isPublic) {
        this.title = title;
        this.content = content;
        this.isPushSend = isPushSend != null ? isPushSend : false;
        this.isPublic = isPublic != null ? isPublic : true;
    }
} 