package com.onebyone.kindergarten.domain.notice.repository;

import com.onebyone.kindergarten.domain.notice.dto.response.NoticeResponseDTO;
import com.onebyone.kindergarten.domain.notice.entity.Notice;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

  @Query(
      "SELECT new com.onebyone.kindergarten.domain.notice.dto.response.NoticeResponseDTO("
          + "n.id, n.title, n.content, n.isPushSend, n.isPublic, n.createdAt, n.updatedAt) "
          + "FROM Notice n "
          + "WHERE n.isPublic = true "
          + "ORDER BY n.createdAt DESC")
  List<NoticeResponseDTO> findPublicNoticeDtos();

  @Query(
      "SELECT new com.onebyone.kindergarten.domain.notice.dto.response.NoticeResponseDTO("
          + "n.id, n.title, n.content, n.isPushSend, n.isPublic, n.createdAt, n.updatedAt) "
          + "FROM Notice n "
          + "ORDER BY n.createdAt DESC")
  List<NoticeResponseDTO> findAllNoticeDtos();
}
