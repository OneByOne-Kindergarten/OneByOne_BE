package com.onebyone.kindergarten.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.onebyone.kindergarten.domain.user.entity.EmailCertification;

public interface EmailCertificationRepository extends JpaRepository<EmailCertification, String> {
    EmailCertification findByEmail(String email);

    boolean existsByEmail(String email);
}
