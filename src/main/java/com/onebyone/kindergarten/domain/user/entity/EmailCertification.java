package com.onebyone.kindergarten.domain.user.entity;

import com.onebyone.kindergarten.global.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "email_certification")
public class EmailCertification extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long emailCertificationId;

    private String email;

    private String code;

    private boolean isCertificated;

    public void completeCertification() {
        this.isCertificated = true;
        this.updatedAt = LocalDateTime.now();
    }
}
