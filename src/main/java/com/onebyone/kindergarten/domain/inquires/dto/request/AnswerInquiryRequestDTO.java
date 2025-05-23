package com.onebyone.kindergarten.domain.inquires.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AnswerInquiryRequestDTO {
    @NotBlank(message = "답변은 필수 항목입니다.")
    @Size(max = 1000, message = "답변은 1000자 이내로 작성해주세요.")
    private String answer;
} 