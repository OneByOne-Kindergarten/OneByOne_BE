package com.onebyone.kindergarten.domain.userFavoriteKindergartens.controller;

import com.onebyone.kindergarten.global.common.ResponseDto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import com.onebyone.kindergarten.domain.userFavoriteKindergartens.dto.response.FavoriteKindergartenResponseDTO;
import com.onebyone.kindergarten.domain.userFavoriteKindergartens.dto.request.ToggleFavoriteRequestDTO;
import com.onebyone.kindergarten.domain.userFavoriteKindergartens.service.UserFavoriteKindergartenService;

import java.util.List;

@RestController
@RequestMapping("/favorite-kindergartens")
@Tag(name = "UserFavoriteKindergarten", description = "유치원 즐겨찾기 API")
@RequiredArgsConstructor
public class UserFavoriteKindergartenController {
    private final UserFavoriteKindergartenService favoriteService;

    @PostMapping
    @Operation(summary = "유치원 즐겨찾기 토글", description = "유치원을 즐겨찾기에 추가하거나 제거합니다.")
    public ResponseDto<FavoriteKindergartenResponseDTO> toggleFavorite(
            @Valid @RequestBody ToggleFavoriteRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseDto.success(
                favoriteService.toggleFavorite(userDetails.getUsername(), request.getKindergartenId())
        );
    }

    @GetMapping
    @Operation(summary = "즐겨찾기 목록 조회", description = "자신의 즐겨찾기 유치원 목록을 조회합니다.")
    public ResponseDto<List<FavoriteKindergartenResponseDTO>> getMyFavorites(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseDto.success(
                favoriteService.getMyFavorites(userDetails.getUsername())
        );
    }

    @GetMapping("/status")
    @Operation(summary = "즐겨찾기 상태 확인", description = "특정 유치원의 즐겨찾기 상태를 확인합니다.")
    public ResponseDto<Boolean> getFavoriteStatus(
            @RequestParam Long kindergartenId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseDto.success(
                favoriteService.isFavorite(userDetails.getUsername(), kindergartenId)
        );
    }
}
