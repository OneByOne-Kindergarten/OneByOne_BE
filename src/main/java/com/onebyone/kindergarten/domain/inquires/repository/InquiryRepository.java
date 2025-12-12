package com.onebyone.kindergarten.domain.inquires.repository;

import com.onebyone.kindergarten.domain.inquires.dto.response.InquiryResponseDTO;
import com.onebyone.kindergarten.domain.inquires.entity.Inquiry;
import com.onebyone.kindergarten.domain.inquires.enums.InquiryStatus;
import com.onebyone.kindergarten.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

  @Query(
      "SELECT new com.onebyone.kindergarten.domain.inquires.dto.response.InquiryResponseDTO("
          + "i.id, i.title, i.content, i.answer, i.status, i.createdAt, "
          + "u.id, u.nickname, u.role) "
          + "FROM Inquiry i "
          + "JOIN i.user u "
          + "WHERE i.user = :user "
          + "ORDER BY i.createdAt DESC")
  Page<InquiryResponseDTO> findDtosByUser(@Param("user") User user, Pageable pageable);

  @Query(
      "SELECT new com.onebyone.kindergarten.domain.inquires.dto.response.InquiryResponseDTO("
          + "i.id, i.title, i.content, i.answer, i.status, i.createdAt, "
          + "u.id, u.nickname, u.role) "
          + "FROM Inquiry i "
          + "JOIN i.user u "
          + "ORDER BY CASE WHEN i.status = 'PENDING' THEN 0 ELSE 1 END, i.createdAt DESC")
  Page<InquiryResponseDTO> findAllDtosOrderByStatusAndCreatedAt(Pageable pageable);

  @Query(
      "SELECT new com.onebyone.kindergarten.domain.inquires.dto.response.InquiryResponseDTO("
          + "i.id, i.title, i.content, i.answer, i.status, i.createdAt, "
          + "u.id, u.nickname, u.role) "
          + "FROM Inquiry i "
          + "JOIN i.user u "
          + "WHERE i.status = :status "
          + "ORDER BY i.createdAt DESC")
  Page<InquiryResponseDTO> findDtosByStatus(
      @Param("status") InquiryStatus status, Pageable pageable);

  @Query("SELECT i FROM Inquiry i " + "JOIN FETCH i.user " + "WHERE i.id = :id")
  Optional<Inquiry> findByIdWithUser(@Param("id") Long id);
}
