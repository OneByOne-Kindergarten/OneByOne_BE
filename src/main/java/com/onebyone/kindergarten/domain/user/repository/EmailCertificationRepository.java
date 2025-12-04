package com.onebyone.kindergarten.domain.user.repository;

import com.onebyone.kindergarten.domain.user.enums.EmailCertificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import com.onebyone.kindergarten.domain.user.entity.EmailCertification;

public interface EmailCertificationRepository extends JpaRepository<EmailCertification, String> {
    EmailCertification findByEmail(String email);

    EmailCertification findByEmailAndCodeAndTypeAndDeletedAtIsNull(String email, String code, EmailCertificationType type);

    boolean existsByEmail(String email);
}
