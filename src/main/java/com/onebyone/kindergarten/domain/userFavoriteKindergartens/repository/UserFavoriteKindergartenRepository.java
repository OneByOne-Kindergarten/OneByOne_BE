package com.onebyone.kindergarten.domain.userFavoriteKindergartens.repository;

import com.onebyone.kindergarten.domain.kindergatens.entity.Kindergarten;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.domain.userFavoriteKindergartens.entity.UserFavoriteKindergarten;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFavoriteKindergartenRepository extends JpaRepository<UserFavoriteKindergarten, Long> {
    @Query("SELECT uf FROM user_favorite_kindergarten uf " +
           "JOIN FETCH uf.kindergarten k " +
           "WHERE uf.user = :user")
    List<UserFavoriteKindergarten> findAllByUserWithKindergarten(@Param("user") User user);
    
    @Query("SELECT uf FROM user_favorite_kindergarten uf " +
           "JOIN FETCH uf.kindergarten k " +
           "WHERE uf.user = :user AND uf.kindergarten = :kindergarten")
    Optional<UserFavoriteKindergarten> findByUserAndKindergartenWithFetch(
            @Param("user") User user, 
            @Param("kindergarten") Kindergarten kindergarten
    );

    @Query("SELECT COUNT(uf) > 0 FROM user_favorite_kindergarten uf " +
           "WHERE uf.user = :user AND uf.kindergarten = :kindergarten")
    boolean existsByUserAndKindergarten(
            @Param("user") User user, 
            @Param("kindergarten") Kindergarten kindergarten
    );
}
