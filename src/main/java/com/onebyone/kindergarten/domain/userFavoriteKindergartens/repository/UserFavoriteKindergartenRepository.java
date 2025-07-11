package com.onebyone.kindergarten.domain.userFavoriteKindergartens.repository;

import com.onebyone.kindergarten.domain.kindergatens.dto.KindergartenResponseDTO;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.domain.userFavoriteKindergartens.entity.UserFavoriteKindergarten;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFavoriteKindergartenRepository extends JpaRepository<UserFavoriteKindergarten, Long> {

    @Query("SELECT new com.onebyone.kindergarten.domain.kindergatens.dto.KindergartenResponseDTO(" +
            "   k.id, k.name, k.establishment, k.establishmentDate, k.openDate ,k.address, " +
            "   k.homepage, k.phoneNumber, k.classCount3, k.classCount4, k.classCount5, " +
            "   k.pupilCount3, k.pupilCount4, k.pupilCount5, k.mixPupilCount, " +
            "   k.specialPupilCount, k.latitude, k.longitude) " +
            "FROM user_favorite_kindergarten uf " +
            "JOIN uf.kindergarten k " +
            "WHERE uf.user = :user")
    List<KindergartenResponseDTO> findDtosByUser(@Param("user") User user);

    @Modifying
    @Query("DELETE FROM user_favorite_kindergarten uf " +
            "WHERE uf.user = :user AND uf.kindergarten.id = :kindergartenId")
    void deleteByUserAndKindergartenId(@Param("user") User user, @Param("kindergartenId") Long kindergartenId);

    @Query("SELECT COUNT(uf) > 0 FROM user_favorite_kindergarten uf " +
            "WHERE uf.user = :user AND uf.kindergarten.id = :kindergartenId")
    boolean existsByUserAndKindergartenId(@Param("user") User user, @Param("kindergartenId") Long kindergartenId);

    /// 사용자의 즐겨찾기 엔티티 목록 조회
    @EntityGraph(attributePaths = {"kindergarten", "kindergarten.kindergartenInternshipReviewAggregate", "kindergarten.kindergartenWorkReviewAggregate"})
    @Query("SELECT uf FROM user_favorite_kindergarten uf WHERE uf.user = :user")
    List<UserFavoriteKindergarten> findByUser(@Param("user") User user);
}
