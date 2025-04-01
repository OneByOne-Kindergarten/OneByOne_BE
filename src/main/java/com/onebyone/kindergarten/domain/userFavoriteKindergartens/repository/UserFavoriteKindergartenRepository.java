package com.onebyone.kindergarten.domain.userFavoriteKindergartens.repository;

import com.onebyone.kindergarten.domain.kindergatens.entity.Kindergarten;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.domain.userFavoriteKindergartens.entity.UserFavoriteKindergarten;
import com.onebyone.kindergarten.domain.userFavoriteKindergartens.dto.response.FavoriteKindergartenResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFavoriteKindergartenRepository extends JpaRepository<UserFavoriteKindergarten, Long> {
    
    @Query("SELECT new com.onebyone.kindergarten.domain.userFavoriteKindergartens.dto.response.FavoriteKindergartenResponseDTO(" +
           "uf.id, k.id, k.name, k.address, uf.createdAt) " +
           "FROM user_favorite_kindergarten uf " +
           "JOIN uf.kindergarten k " +
           "WHERE uf.user = :user")
    List<FavoriteKindergartenResponseDTO> findDtosByUser(@Param("user") User user);

    @Modifying
    @Query("DELETE FROM user_favorite_kindergarten uf " +
           "WHERE uf.user = :user AND uf.kindergarten.id = :kindergartenId")
    void deleteByUserAndKindergartenId(@Param("user") User user, @Param("kindergartenId") Long kindergartenId);

    @Query("SELECT COUNT(uf) > 0 FROM user_favorite_kindergarten uf " +
           "WHERE uf.user = :user AND uf.kindergarten.id = :kindergartenId")
    boolean existsByUserAndKindergartenId(@Param("user") User user, @Param("kindergartenId") Long kindergartenId);
}
