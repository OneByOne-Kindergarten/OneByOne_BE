package com.onebyone.kindergarten.domain.inquires.repository;

import com.onebyone.kindergarten.domain.inquires.entity.Inquiry;
import com.onebyone.kindergarten.domain.inquires.enums.InquiryStatus;
import com.onebyone.kindergarten.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    // 사용자별 문의 조회
    @Query("SELECT i FROM Inquiry i WHERE i.user = :user ORDER BY i.createdAt DESC")
    Page<Inquiry> findByUser(@Param("user") User user, Pageable pageable);
    
    // 모든 문의 조회 (관리자용)
    @Query("SELECT i FROM Inquiry i ORDER BY CASE WHEN i.status = 'PENDING' THEN 0 ELSE 1 END, i.createdAt DESC")
    Page<Inquiry> findAllOrderByStatusAndCreatedAt(Pageable pageable);
    
    // 상태별 문의 조회
    Page<Inquiry> findByStatusOrderByCreatedAtDesc(InquiryStatus status, Pageable pageable);
}
