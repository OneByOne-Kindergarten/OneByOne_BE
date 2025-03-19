package com.onebyone.kindergarten.domain.user.repository;

import com.onebyone.kindergarten.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmail(String email);
    Optional<User> findByEmailAndDeletedAtIsNull(String email);
}
