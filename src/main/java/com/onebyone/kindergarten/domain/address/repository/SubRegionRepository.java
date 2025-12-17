package com.onebyone.kindergarten.domain.address.repository;

import com.onebyone.kindergarten.domain.address.entity.SubRegion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubRegionRepository extends JpaRepository<SubRegion, Long> {
  SubRegion findBySubRegionId(Long subRegionId);

  List<SubRegion> findAllByParent(SubRegion subRegion);
}
