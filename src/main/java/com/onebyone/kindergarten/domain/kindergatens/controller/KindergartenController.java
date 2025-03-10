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



}
