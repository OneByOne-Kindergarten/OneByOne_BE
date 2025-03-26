package com.onebyone.kindergarten.domain.kindergartenWorkHistories.repository;

import com.onebyone.kindergarten.domain.kindergartenWorkHistories.entity.KindergartenWorkHistory;
import com.onebyone.kindergarten.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KindergartenWorkHistoryRepository extends JpaRepository<KindergartenWorkHistory, Long> {
    List<KindergartenWorkHistory> findByUserOrderByStartDateDesc(User user);
} 