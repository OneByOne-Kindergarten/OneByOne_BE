package com.onebyone.kindergarten.domain.address.repository;

import com.onebyone.kindergarten.domain.address.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
}
