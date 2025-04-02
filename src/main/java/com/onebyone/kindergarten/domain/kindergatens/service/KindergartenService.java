package com.onebyone.kindergarten.domain.kindergatens.service;

import com.onebyone.kindergarten.domain.kindergatens.dto.KindergartenDTO;
import com.onebyone.kindergarten.domain.kindergatens.entity.Kindergarten;
import com.onebyone.kindergarten.domain.kindergatens.repository.KindergartenRepository;
import com.onebyone.kindergarten.global.exception.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.onebyone.kindergarten.domain.kindergatens.dto.KindergartenSearchDTO;
import com.onebyone.kindergarten.domain.kindergatens.dto.KindergartenResponseDTO;

import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class KindergartenService {

    private final KindergartenRepository kindergartenRepository;

    /// 유치원 정보 저장
    @Transactional
    public List<Kindergarten> saveAll(List<KindergartenDTO> kindergartenDTOs) {
        List<Kindergarten> updatedKindergartens = new ArrayList<>();

        for (KindergartenDTO dto : kindergartenDTOs) {
            // 유치원 이름과 주소로 검색
            kindergartenRepository.findByNameAndAddress(dto.getName(), dto.getAddress())
                    .ifPresentOrElse(
                            // 존재하는 경우 - 정보 업데이트
                            existingKindergarten -> {
                                existingKindergarten.update(dto);
                                updatedKindergartens.add(existingKindergarten);
                            },
                            // 존재하지 않는 경우 - 새로 생성
                            () -> {
                                Kindergarten newKindergarten = convertToEntity(dto);
                                updatedKindergartens.add(kindergartenRepository.save(newKindergarten));
                            }
                    );
        }

        return updatedKindergartens;
    }

    /// DTO -> Entity 변환
    private Kindergarten convertToEntity(KindergartenDTO dto) {
        return Kindergarten.builder()
                .name(dto.getName())
                .establishment(dto.getEstablishment())
                .establishmentDate(dto.getEstablishmentDate())
                .openDate(dto.getOpenDate())
                .address(dto.getAddress())
                .homepage(dto.getHomepage())
                .phoneNumber(dto.getPhoneNumber())
                .classCount3(dto.getClassCount3())
                .classCount4(dto.getClassCount4())
                .classCount5(dto.getClassCount5())
                .pupilCount3(dto.getPupilCount3())
                .pupilCount4(dto.getPupilCount4())
                .pupilCount5(dto.getPupilCount5())
                .mixPupilCount(dto.getMixPupilCount())
                .specialPupilCount(dto.getSpecialPupilCount())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .build();
    }

    /// 유치원 검색
    public Page<KindergartenResponseDTO> searchKindergartens(KindergartenSearchDTO searchDTO, Pageable pageable) {
        return kindergartenRepository.findBySearchCriteria(searchDTO, pageable)
                .map(KindergartenResponseDTO::from);
    }

    /// 유치원 상세 조회
    public KindergartenResponseDTO findById(Long id) {
        Kindergarten kindergarten = kindergartenRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("유치원을 찾을 수 없습니다. ID: " + id));
        return KindergartenResponseDTO.from(kindergarten);
    }

}
