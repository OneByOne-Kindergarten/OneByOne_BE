package com.onebyone.kindergarten.domain.userFavoriteKindergartens.service;

import com.onebyone.kindergarten.domain.kindergartenWorkHistories.exception.KindergartenNotFoundException;
import com.onebyone.kindergarten.domain.kindergatens.entity.Kindergarten;
import com.onebyone.kindergarten.domain.kindergatens.repository.KindergartenRepository;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.domain.user.service.UserService;
import com.onebyone.kindergarten.domain.userFavoriteKindergartens.dto.response.FavoriteKindergartenResponseDTO;
import com.onebyone.kindergarten.domain.userFavoriteKindergartens.dto.response.FavoriteToggleResponseDTO;
import com.onebyone.kindergarten.domain.userFavoriteKindergartens.entity.UserFavoriteKindergarten;
import com.onebyone.kindergarten.domain.userFavoriteKindergartens.repository.UserFavoriteKindergartenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserFavoriteKindergartenService {

    private final UserFavoriteKindergartenRepository favoriteRepository;
    private final UserService userService;
    private final KindergartenRepository kindergartenRepository;

    /// 유치원 즐겨찾기 토글
    @Transactional
    public FavoriteToggleResponseDTO toggleFavorite(String email, Long kindergartenId) {

        // 사용자 조회
        User user = userService.getUserByEmail(email);
        
        // 즐겨찾기 존재 여부 확인 및 삭제 시도
        boolean existed = favoriteRepository.existsByUserAndKindergartenId(user, kindergartenId);
        
        if (existed) {
            // 존재 - 삭제
            favoriteRepository.deleteByUserAndKindergartenId(user, kindergartenId);
            return new FavoriteToggleResponseDTO(false);
        } else {
            // 존재 하지 않음 - 추가
            UserFavoriteKindergarten favorite = UserFavoriteKindergarten.builder()
                    .user(user)
                    .kindergarten(Kindergarten.builder().id(kindergartenId).build())
                    .build();
            favoriteRepository.save(favorite);
            return new FavoriteToggleResponseDTO(true);
        }
    }

    /// 즐겨찾기 목록 조회
    public List<FavoriteKindergartenResponseDTO> getMyFavorites(String email) {

        // 사용자 조회
        User user = userService.getUserByEmail(email);

        // 즐겨찾기 목록 조회
        return favoriteRepository.findDtosByUser(user);
    }

    /// 즐겨찾기 상태 확인
    public boolean isFavorite(String email, Long kindergartenId) {

        // 사용자 조회
        User user = userService.getUserByEmail(email);

        // 유치원 존재 여부 확인
        return favoriteRepository.existsByUserAndKindergartenId(user, kindergartenId);
    }
}