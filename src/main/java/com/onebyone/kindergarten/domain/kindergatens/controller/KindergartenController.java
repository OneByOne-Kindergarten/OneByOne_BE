package com.onebyone.kindergarten.domain.kindergatens.controller;

import com.onebyone.kindergarten.domain.kindergatens.dto.KindergartenDTO;
import com.onebyone.kindergarten.domain.kindergatens.entity.Kindergarten;
import com.onebyone.kindergarten.domain.kindergatens.service.KindergartenService;
import com.onebyone.kindergarten.global.common.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import com.onebyone.kindergarten.domain.kindergatens.dto.KindergartenSearchDTO;
import com.onebyone.kindergarten.domain.kindergatens.dto.KindergartenResponseDTO;
import com.onebyone.kindergarten.global.common.PageResponseDTO;

import java.util.List;

@RestController
@RequestMapping("/kindergarten")
@RequiredArgsConstructor
@Tag(name = "유치원", description = "유치원 API")
public class KindergartenController {

    private final KindergartenService kindergartenService;

    @PostMapping("/batch")
    @Operation(summary = "유치원 정보 저장",
            description = "global/docs/kindergartens.json 파일을 사용하여 유치원 정보를 저장합니다.")
    public ResponseEntity<Boolean> saveKindergartens(@RequestBody List<KindergartenDTO> kindergartenDTOs) {
        return ResponseEntity.ok(kindergartenService.saveAll(kindergartenDTOs));
    }

    @GetMapping
    @Operation(summary = "유치원 검색",
            description = "이름, 설립 유형, 주소, 학급 수, 원생 수, 위도, 경도, 반경으로 유치원을 검색합니다.")
    public ResponseEntity<PageResponseDTO<KindergartenResponseDTO>> searchKindergartens(
            KindergartenSearchDTO searchDTO,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<KindergartenResponseDTO> page = kindergartenService.searchKindergartens(searchDTO, pageable);
        return ResponseEntity.ok(new PageResponseDTO<>(page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "유치원 상세 조회",
            description = "유치원의 ID를 사용하여 유치원 정보를 조회합니다.")
    public ResponseEntity<KindergartenResponseDTO> getKindergarten(@PathVariable Long id) {
        return ResponseEntity.ok(kindergartenService.findById(id));
    }


    @GetMapping("/nearby")
    @Operation(summary = "주변 유치원 조회",
            description = "현재 위치 기준으로 특정 반경 내의 유치원을 조회합니다.")
    public ResponseDto<List<KindergartenResponseDTO>> getNearby(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "2.0") double radiusKm
    ) {
        return ResponseDto.success(
                kindergartenService.getNearbyKindergarten(
                        latitude,
                        longitude,
                        radiusKm
                )
        );
    }

}
