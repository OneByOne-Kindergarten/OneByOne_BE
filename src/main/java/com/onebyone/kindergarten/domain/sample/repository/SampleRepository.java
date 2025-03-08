package com.onebyone.kindergarten.domain.sample.repository;

import com.onebyone.kindergarten.domain.sample.entity.Sample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SampleRepository extends JpaRepository<Sample, Long> {}
