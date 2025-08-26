package com.onebyone.kindergarten.domain.user.repository;

import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.data.repository.CrudRepository;

import com.onebyone.kindergarten.domain.user.entity.EmailCertification;

public interface EmailCertificationRepository extends KeyValueRepository<EmailCertification, String> {

}
