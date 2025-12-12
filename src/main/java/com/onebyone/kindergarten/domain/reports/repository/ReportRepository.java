package com.onebyone.kindergarten.domain.reports.repository;

import com.onebyone.kindergarten.domain.reports.dto.response.ReportResponseDTO;
import com.onebyone.kindergarten.domain.reports.entity.Report;
import com.onebyone.kindergarten.domain.reports.enums.ReportTargetType;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.global.enums.ReportStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

  @Query(
      "SELECT new com.onebyone.kindergarten.domain.reports.dto.response.ReportResponseDTO("
          + "r.id, u.nickname, r.targetId, r.targetType, r.reason, r.status, r.createdAt) "
          + "FROM Report r "
          + "JOIN r.reporter u "
          + "WHERE r.reporter = :reporter")
  Page<ReportResponseDTO> findDtosByReporter(@Param("reporter") User reporter, Pageable pageable);

  @Query(
      "SELECT new com.onebyone.kindergarten.domain.reports.dto.response.ReportResponseDTO("
          + "r.id, u.nickname, r.targetId, r.targetType, r.reason, r.status, r.createdAt) "
          + "FROM Report r "
          + "JOIN r.reporter u "
          + "WHERE (:status is null OR r.status = :status) "
          + "AND (:targetType is null OR r.targetType = :targetType)")
  Page<ReportResponseDTO> findAllDtosByCondition(
      @Param("status") ReportStatus status,
      @Param("targetType") ReportTargetType targetType,
      Pageable pageable);

  @Query("SELECT r FROM Report r " + "JOIN FETCH r.reporter " + "WHERE r.id = :id")
  Optional<Report> findByIdWithReporter(@Param("id") Long id);
}
