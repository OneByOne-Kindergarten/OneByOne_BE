package com.onebyone.kindergarten.domain.kindergatens.repository;

import com.onebyone.kindergarten.domain.kindergatens.entity.Kindergarten;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.onebyone.kindergarten.domain.kindergatens.dto.KindergartenSearchDTO;

import java.util.Optional;

@Repository
public interface KindergartenRepository extends JpaRepository<Kindergarten, Long> {

    /// 유치원 검색 - 이름, 설립 유형, 주소, 학급 수, 원생 수, 위도, 경도, 반경
    @Query("SELECT k FROM Kindergarten k " +
           "WHERE (:#{#search.name} IS NULL OR k.name LIKE %:#{#search.name}%) " +
           "AND (:#{#search.establishment} IS NULL OR k.establishment = :#{#search.establishment}) " +
           "AND (:#{#search.address} IS NULL OR k.address LIKE %:#{#search.address}%) " +
           "AND (:#{#search.minClassCount} IS NULL OR (k.classCount3 + k.classCount4 + k.classCount5) >= :#{#search.minClassCount}) " +
           "AND (:#{#search.maxClassCount} IS NULL OR (k.classCount3 + k.classCount4 + k.classCount5) <= :#{#search.maxClassCount}) " +
           "AND (:#{#search.minPupilCount} IS NULL OR (k.pupilCount3 + k.pupilCount4 + k.pupilCount5 + k.mixPupilCount + k.specialPupilCount) >= :#{#search.minPupilCount}) " +
           "AND (:#{#search.maxPupilCount} IS NULL OR (k.pupilCount3 + k.pupilCount4 + k.pupilCount5 + k.mixPupilCount + k.specialPupilCount) <= :#{#search.maxPupilCount}) " +
           "AND (:#{#search.latitude} IS NULL OR :#{#search.longitude} IS NULL OR :#{#search.radius} IS NULL OR " +
           "6371 * acos(cos(radians(:#{#search.latitude})) * cos(radians(k.latitude)) * cos(radians(k.longitude) - radians(:#{#search.longitude})) + sin(radians(:#{#search.latitude})) * sin(radians(k.latitude))) <= :#{#search.radius})")
    Page<Kindergarten> findBySearchCriteria(@Param("search") KindergartenSearchDTO search, Pageable pageable);

    /// 유치원 이름으로 검색
    Optional<Kindergarten> findByName(String name);
}
