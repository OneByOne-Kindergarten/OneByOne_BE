package com.onebyone.kindergarten.domain.userFavoriteKindergartens.service;

import com.onebyone.kindergarten.domain.kindergartenWorkHistories.exception.KindergartenNotFoundException;
import com.onebyone.kindergarten.domain.kindergatens.entity.Kindergarten;
import com.onebyone.kindergarten.domain.kindergatens.repository.KindergartenRepository;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.domain.user.service.UserService;
import com.onebyone.kindergarten.domain.userFavoriteKindergartens.dto.response.FavoriteKindergartenResponseDTO;
import com.onebyone.kindergarten.domain.userFavoriteKindergartens.entity.UserFavoriteKindergarten;
import com.onebyone.kindergarten.domain.userFavoriteKindergartens.repository.UserFavoriteKindergartenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserFavoriteKindergartenService {
    private final UserFavoriteKindergartenRepository favoriteRepository;
    private final UserService userService;
    private final KindergartenRepository kindergartenRepository;

    public UserFavoriteKindergartenService(
            UserFavoriteKindergartenRepository favoriteRepository,
            UserService userService,
            KindergartenRepository kindergartenRepository) {
        this.favoriteRepository = favoriteRepository;
        this.userService = userService;
        this.kindergartenRepository = kindergartenRepository;
    }

    @Transactional
    public FavoriteKindergartenResponseDTO toggleFavorite(String email, Long kindergartenId) {
        User user = userService.getUserByEmail(email);
        Kindergarten kindergarten = kindergartenRepository.findById(kindergartenId)
                .orElseThrow(KindergartenNotFoundException::new);

        Optional<UserFavoriteKindergarten> existingFavorite = 
                favoriteRepository.findByUserAndKindergartenWithFetch(user, kindergarten);

        if (existingFavorite.isPresent()) {
            favoriteRepository.delete(existingFavorite.get());
            return null;
        }

        UserFavoriteKindergarten favorite = UserFavoriteKindergarten.builder()
                .user(user)
                .kindergarten(kindergarten)
                .build();

        return FavoriteKindergartenResponseDTO.fromEntity(favoriteRepository.save(favorite));
    }

    public List<FavoriteKindergartenResponseDTO> getMyFavorites(String email) {
        User user = userService.getUserByEmail(email);
        return favoriteRepository.findAllByUserWithKindergarten(user)
                .stream()
                .map(FavoriteKindergartenResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public boolean isFavorite(String email, Long kindergartenId) {
        User user = userService.getUserByEmail(email);
        Kindergarten kindergarten = kindergartenRepository.findById(kindergartenId)
                .orElseThrow(KindergartenNotFoundException::new);

        return favoriteRepository.existsByUserAndKindergarten(user, kindergarten);
    }
}