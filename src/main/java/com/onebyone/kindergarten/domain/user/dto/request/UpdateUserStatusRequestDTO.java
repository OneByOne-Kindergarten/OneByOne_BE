package com.onebyone.kindergarten.domain.user.dto.request;

import com.onebyone.kindergarten.domain.user.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "유저 상태 변경 요청 DTO")
public class UpdateUserStatusRequestDTO {

  @NotNull(message = "상태는 필수입니다.") @Schema(description = "변경할 유저 상태", example = "ACTIVE")
  private UserStatus status;

  @Schema(description = "상태 변경 사유", example = "부적절한 게시물 작성으로 인한 정지")
  private String reason;
}
