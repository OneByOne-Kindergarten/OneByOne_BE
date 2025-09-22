package com.onebyone.kindergarten.domain.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "email_certification")
public class EmailCertification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long emailCertificationId;

    private String email;

    private String code;

    private boolean isCertificated;

    public void completeCertification() {
        this.isCertificated = true;
    }
}
