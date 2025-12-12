package com.onebyone.kindergarten.domain.user.dto.request;

import lombok.Data;

@Data
public class CheckEmailCertificationRequestDTO {
  private String email;
  private String certification;
}
