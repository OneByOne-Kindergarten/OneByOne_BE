package com.onebyone.kindergarten.domain.user.repository;

import com.onebyone.kindergarten.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmail(String email);

    Optional<User> findByEmailAndDeletedAtIsNull(String email);

    @Query("SELECT u FROM user u LEFT JOIN FETCH u.Kindergarten WHERE u.email = :email AND u.deletedAt IS NULL")
    Optional<User> findUserWithKindergarten(@Param("email") String email);

    Optional<User> findByEmailAndDeletedAtIsNotNull(String email);

    @Query("SELECT u FROM user u LEFT JOIN FETCH u.Kindergarten WHERE u.id = :id")
    Optional<User> findByIdWithKindergarten(@Param("id") Long id);

    @Query("SELECT u FROM user u LEFT JOIN FETCH u.Kindergarten k")
    Page<User> findAllUsersWithKindergarten(Pageable pageable);

    @Query(value = "SELECT u FROM user u LEFT JOIN FETCH u.Kindergarten k WHERE " +
            "(:email IS NULL OR " +
            "  (LENGTH(:email) <= 3 AND u.email LIKE %:email%) OR " +
            "  (LENGTH(:email) > 3 AND u.email LIKE :email%)) AND " +
            "(:nickname IS NULL OR " +
            "  (LENGTH(:nickname) <= 2 AND u.nickname LIKE %:nickname%) OR " +
            "  (LENGTH(:nickname) > 2 AND u.nickname LIKE :nickname%)) AND " +
            "(:role IS NULL OR u.role = :role) AND " +
            "(:provider IS NULL OR u.provider = :provider) AND " +
            "(:status IS NULL OR u.status = :status) AND " +
            "(:kindergartenName IS NULL OR k.name LIKE %:kindergartenName%) AND " +
            "(:hasWrittenReview IS NULL OR u.hasWrittenReview = :hasWrittenReview) AND " +
            "(:isRestoredUser IS NULL OR " +
            "  (:isRestoredUser = true AND u.previousDeletedAt IS NOT NULL) OR " +
            "  (:isRestoredUser = false AND u.previousDeletedAt IS NULL))",
            countQuery = "SELECT COUNT(u) FROM user u LEFT JOIN u.Kindergarten k WHERE " +
            "(:email IS NULL OR " +
            "  (LENGTH(:email) <= 3 AND u.email LIKE %:email%) OR " +
            "  (LENGTH(:email) > 3 AND u.email LIKE :email%)) AND " +
            "(:nickname IS NULL OR " +
            "  (LENGTH(:nickname) <= 2 AND u.nickname LIKE %:nickname%) OR " +
            "  (LENGTH(:nickname) > 2 AND u.nickname LIKE :nickname%)) AND " +
            "(:role IS NULL OR u.role = :role) AND " +
            "(:provider IS NULL OR u.provider = :provider) AND " +
            "(:status IS NULL OR u.status = :status) AND " +
            "(:kindergartenName IS NULL OR k.name LIKE %:kindergartenName%) AND " +
            "(:hasWrittenReview IS NULL OR u.hasWrittenReview = :hasWrittenReview) AND " +
            "(:isRestoredUser IS NULL OR " +
            "  (:isRestoredUser = true AND u.previousDeletedAt IS NOT NULL) OR " +
            "  (:isRestoredUser = false AND u.previousDeletedAt IS NULL))")
    Page<User> findUsersWithFilters(
            @Param("email") String email,
            @Param("nickname") String nickname,
            @Param("role") com.onebyone.kindergarten.domain.user.enums.UserRole role,
            @Param("provider") com.onebyone.kindergarten.domain.user.entity.UserProvider provider,
            @Param("status") com.onebyone.kindergarten.domain.user.enums.UserStatus status,
            @Param("kindergartenName") String kindergartenName,
            @Param("hasWrittenReview") Boolean hasWrittenReview,
            @Param("isRestoredUser") Boolean isRestoredUser,
            Pageable pageable
    );

    /// 모든 활성 사용자 조회
    @Query("SELECT u FROM user u WHERE u.deletedAt IS NULL")
    List<User> findAllActiveUsers();
}
