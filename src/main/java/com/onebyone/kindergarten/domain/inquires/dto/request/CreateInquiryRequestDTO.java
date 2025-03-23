package com.onebyone.kindergarten.domain.inquires.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateInquiryRequestDTO {
    @NotBlank(message = "제목은 필수 항목입니다.")
    @Size(max = 100, message = "제목은 100자 이내로 작성해주세요.")
    private String title;
    
    @NotBlank(message = "내용은 필수 항목입니다.")
    @Size(max = 1000, message = "내용은 1000자 이내로 작성해주세요.")
    private String content;
} 