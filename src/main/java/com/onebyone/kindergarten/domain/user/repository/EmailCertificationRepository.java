package com.onebyone.kindergarten.domain.user.repository;

import org.springframework.data.repository.CrudRepository;

import com.onebyone.kindergarten.domain.user.entity.EmailCertification;

public interface EmailCertificationRepository extends CrudRepository<EmailCertification, String> {

}
