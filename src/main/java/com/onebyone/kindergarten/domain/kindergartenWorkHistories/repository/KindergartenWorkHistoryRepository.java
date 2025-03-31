package com.onebyone.kindergarten.domain.kindergartenWorkHistories.repository;

import com.onebyone.kindergarten.domain.kindergartenWorkHistories.dto.KindergartenWorkHistoryResponse;
import com.onebyone.kindergarten.domain.kindergartenWorkHistories.entity.KindergartenWorkHistory;
import com.onebyone.kindergarten.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface KindergartenWorkHistoryRepository extends JpaRepository<KindergartenWorkHistory, Long> {

    @Query("SELECT new com.onebyone.kindergarten.domain.kindergartenWorkHistories.dto.KindergartenWorkHistoryResponse(" +
           "h.id, k.name, h.startDate, h.endDate, h.workType) " +
           "FROM kindergarten_work_history h " +
           "JOIN h.kindergarten k " +
           "WHERE h.user = :user " +
           "ORDER BY h.startDate DESC")
    List<KindergartenWorkHistoryResponse> findDtosByUser(@Param("user") User user);

    @Query("SELECT h FROM kindergarten_work_history h " +
           "JOIN FETCH h.kindergarten " +
           "WHERE h.id = :id")
    Optional<KindergartenWorkHistory> findByIdWithKindergarten(@Param("id") Long id);
} 