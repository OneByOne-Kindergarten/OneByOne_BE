package com.onebyone.kindergarten.domain.user.entity;

import com.onebyone.kindergarten.domain.user.enums.EmailCertificationType;
import com.onebyone.kindergarten.global.common.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "email_certification")
public class EmailCertification extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long emailCertificationId;

  @Enumerated(EnumType.STRING)
  private EmailCertificationType type;

  private String email;

  private String code;

  private boolean isCertificated;

  public void completeCertification() {
    this.isCertificated = true;
    this.updatedAt = LocalDateTime.now();
  }

  public void updateCode(String code) {
    this.code = code;
  }

  public void markUncertificated() {
    this.isCertificated = false;
  }
}
