package com.onebyone.kindergarten.domain.kindergatens.controller;

import com.onebyone.kindergarten.domain.kindergatens.dto.KindergartenDTO;
import com.onebyone.kindergarten.domain.kindergatens.entity.Kindergarten;
import com.onebyone.kindergarten.domain.kindergatens.service.KindergartenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import com.onebyone.kindergarten.domain.kindergatens.dto.KindergartenSearchDTO;
import com.onebyone.kindergarten.domain.kindergatens.dto.KindergartenResponseDTO;
import com.onebyone.kindergarten.domain.kindergatens.dto.PageResponseDTO;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("/kindergarten")
@RequiredArgsConstructor
public class KindergartenController {

    // KindergartenService 주입
    private final KindergartenService kindergartenService;

    // 유치원 정보 저장 API - global/docs/kindergartens.json 사용
    @PostMapping("/batch")
    public ResponseEntity<List<Kindergarten>> saveKindergartens(@RequestBody List<KindergartenDTO> kindergartenDTOs) {
        return ResponseEntity.ok(kindergartenService.saveAll(kindergartenDTOs));
    }

    /// 유치원 검색 API - 이름, 설립 유형, 주소, 학급 수, 원생 수, 위도, 경도, 반경
    @GetMapping
    public ResponseEntity<PageResponseDTO<KindergartenResponseDTO>> searchKindergartens(
            KindergartenSearchDTO searchDTO,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<KindergartenResponseDTO> page = kindergartenService.searchKindergartens(searchDTO, pageable);
        return ResponseEntity.ok(new PageResponseDTO<>(page));
    }

    /// 유치원 상세 조회 API
    @GetMapping("/{id}")
    public ResponseEntity<KindergartenResponseDTO> getKindergarten(@PathVariable Long id) {
        return ResponseEntity.ok(kindergartenService.findById(id));
    }

}
