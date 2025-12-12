package com.onebyone.kindergarten.domain.userBlock.repository;

import com.onebyone.kindergarten.domain.userBlock.dto.response.BlockedUserResponseDto;
import com.onebyone.kindergarten.domain.userBlock.entity.UserBlock;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBlockRepository extends JpaRepository<UserBlock, Long> {

  @Query("SELECT ub.blockedUser.id FROM UserBlock ub WHERE ub.user.id = :userId")
  List<Long> findBlockedUserIdsByUserId(@Param("userId") Long userId);

  boolean existsByUserIdAndBlockedUserId(Long userId, Long blockedUserId);

  void deleteByUserIdAndBlockedUserId(Long userId, Long blockedUserId);

  @Query(
      "SELECT new com.onebyone.kindergarten.domain.userBlock.dto.response.BlockedUserResponseDto("
          + "bu.email, bu.nickname, bu.role, bu.career, ub.createdAt) "
          + "FROM UserBlock ub "
          + "JOIN ub.blockedUser bu "
          + "WHERE ub.user.id = :userId")
  List<BlockedUserResponseDto> findBlockedUsersByUserId(@Param("userId") Long userId);
}
