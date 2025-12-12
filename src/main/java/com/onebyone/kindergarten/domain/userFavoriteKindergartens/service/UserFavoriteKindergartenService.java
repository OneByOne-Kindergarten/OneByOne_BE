package com.onebyone.kindergarten.domain.userFavoriteKindergartens.service;

import com.onebyone.kindergarten.domain.kindergatens.dto.KindergartenResponseDTO;
import com.onebyone.kindergarten.domain.kindergatens.entity.Kindergarten;
import com.onebyone.kindergarten.domain.kindergatens.repository.KindergartenRepository;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.domain.user.service.UserService;
import com.onebyone.kindergarten.domain.userFavoriteKindergartens.dto.response.FavoriteToggleResponseDTO;
import com.onebyone.kindergarten.domain.userFavoriteKindergartens.entity.UserFavoriteKindergarten;
import com.onebyone.kindergarten.domain.userFavoriteKindergartens.repository.UserFavoriteKindergartenRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserFavoriteKindergartenService {

  private final UserFavoriteKindergartenRepository favoriteRepository;
  private final UserService userService;
  private final KindergartenRepository kindergartenRepository;

  /// 유치원 즐겨찾기 토글
  @Transactional
  public FavoriteToggleResponseDTO toggleFavorite(Long userId, Long kindergartenId) {

    // 사용자 조회
    User user = userService.getUserById(userId);

    // 즐겨찾기 존재 여부 확인 및 삭제 시도
    boolean existed = favoriteRepository.existsByUserAndKindergartenId(user, kindergartenId);

    if (existed) {
      // 존재 - 삭제
      favoriteRepository.deleteByUserAndKindergartenId(user, kindergartenId);
      return new FavoriteToggleResponseDTO(false);
    } else {
      // 존재 하지 않음 - 추가
      UserFavoriteKindergarten favorite =
          UserFavoriteKindergarten.builder()
              .user(user)
              .kindergarten(Kindergarten.builder().id(kindergartenId).build())
              .build();
      favoriteRepository.save(favorite);
      return new FavoriteToggleResponseDTO(true);
    }
  }

  /// 즐겨찾기 목록 조회
  public List<KindergartenResponseDTO> getMyFavorites(Long userId) {

    // 사용자 조회
    User user = userService.getUserById(userId);

    // 즐겨찾기 목록 조회
    List<UserFavoriteKindergarten> favorites = favoriteRepository.findByUser(user);
    return favorites.stream()
        .map(favorite -> KindergartenResponseDTO.from(favorite.getKindergarten()))
        .toList();
  }

  /// 즐겨찾기 상태 확인
  public boolean isFavorite(Long userId, Long kindergartenId) {

    // 사용자 조회
    User user = userService.getUserById(userId);

    // 유치원 존재 여부 확인
    return favoriteRepository.existsByUserAndKindergartenId(user, kindergartenId);
  }
}
