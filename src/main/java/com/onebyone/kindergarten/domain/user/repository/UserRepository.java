package com.onebyone.kindergarten.domain.user.repository;

import com.onebyone.kindergarten.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmail(String email);

    Optional<User> findByEmailAndDeletedAtIsNull(String email);

    @Query("SELECT u FROM user u LEFT JOIN FETCH u.Kindergarten WHERE u.email = :email AND u.deletedAt IS NULL")
    Optional<User> findUserWithKindergarten(@Param("email") String email);
}
