package com.onebyone.kindergarten.domain.communityComments.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCommentRequestDTO {
    @NotBlank(message = "댓글 내용은 필수입니다.")
    @Size(max = 500, message = "댓글은 500자를 초과할 수 없습니다.")
    private String content;
} 