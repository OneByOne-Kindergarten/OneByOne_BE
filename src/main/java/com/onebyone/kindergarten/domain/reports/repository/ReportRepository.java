package com.onebyone.kindergarten.domain.reports.repository;

import com.onebyone.kindergarten.domain.reports.entity.Report;
import com.onebyone.kindergarten.domain.reports.enums.ReportTargetType;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.global.enums.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    Page<Report> findByReporter(User reporter, Pageable pageable);

    @Query("SELECT r FROM Report r " +
           "WHERE (:status is null OR r.status = :status) " +
           "AND (:targetType is null OR r.targetType = :targetType)")
    Page<Report> findAllByCondition(
            @Param("status") ReportStatus status,
            @Param("targetType") ReportTargetType targetType,
            Pageable pageable);
}
