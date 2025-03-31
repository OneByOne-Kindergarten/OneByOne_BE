package com.onebyone.kindergarten.domain.kindergartenWorkHistories.service;

import com.onebyone.kindergarten.domain.kindergartenWorkHistories.dto.KindergartenWorkHistoryRequest;
import com.onebyone.kindergarten.domain.kindergartenWorkHistories.dto.KindergartenWorkHistoryResponse;
import com.onebyone.kindergarten.domain.kindergartenWorkHistories.entity.KindergartenWorkHistory;
import com.onebyone.kindergarten.domain.kindergartenWorkHistories.repository.KindergartenWorkHistoryRepository;
import com.onebyone.kindergarten.domain.kindergatens.entity.Kindergarten;
import com.onebyone.kindergarten.domain.kindergatens.repository.KindergartenRepository;
import com.onebyone.kindergarten.domain.user.entity.User;
import com.onebyone.kindergarten.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import com.onebyone.kindergarten.domain.kindergartenWorkHistories.exception.KindergartenNotFoundException;
import com.onebyone.kindergarten.domain.kindergartenWorkHistories.exception.WorkHistoryNotFoundException;
import com.onebyone.kindergarten.domain.kindergartenWorkHistories.exception.UnauthorizedDeleteException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KindergartenWorkHistoryService {

    private final UserService userService;
    private final KindergartenWorkHistoryRepository workHistoryRepository;
    private final KindergartenRepository kindergartenRepository;

    /// 경력 개월 수 계산
    private int calculateCareerMonths(User user, LocalDate startDate, LocalDate endDate, boolean isAdding) {
        int currentCareerMonths = user.getCareer() == null ? 0 : Integer.parseInt(user.getCareer());
        long monthsBetween = ChronoUnit.MONTHS.between(startDate, endDate);
        return isAdding ? 
            currentCareerMonths + (int)monthsBetween : 
            currentCareerMonths - (int)monthsBetween;
    }

    /// 유치원 근무 이력 추가
    @Transactional
    public KindergartenWorkHistoryResponse addCertification(String email, KindergartenWorkHistoryRequest request) {

        // 유치원 이름으로 유치원 조회
        Kindergarten kindergarten = kindergartenRepository.findByName(request.getKindergartenName())
                .orElseThrow(KindergartenNotFoundException::new);

        // 사용자 조회
        User user = userService.getUserByEmail(email);

        // 유치원 경력 개월 수 업데이트
        int newCareerMonths = calculateCareerMonths(
            user, 
            request.getStartDate(),
            request.getEndDate(), 
            true
        );
        userService.updateCareer(user, String.valueOf(newCareerMonths));

        // 유치원 근무 이력 저장
        KindergartenWorkHistory workHistory = request.toEntity(user, kindergarten);
        workHistoryRepository.save(workHistory);
        
        return KindergartenWorkHistoryResponse.from(workHistory);
    }

    /// 유치원 근무 이력 조회
    public List<KindergartenWorkHistoryResponse> getCertification(String email) {

        // 사용자 조회
        User user = userService.getUserByEmail(email);

        // 유치원 근무 이력 조회
        return workHistoryRepository.findDtosByUser(user);
    }

    /// 유치원 근무 이력 삭제
    @Transactional
    public void deleteCertification(String email, Long historyId) {

        // 유치원 근무 이력 조회
        KindergartenWorkHistory workHistory = workHistoryRepository.findByIdWithKindergarten(historyId)
                .orElseThrow(WorkHistoryNotFoundException::new);

        // 사용자 조회
        User user = userService.getUserByEmail(email);

        // 유치원 근무 이력 소유자 확인
        if (!workHistory.getUser().equals(user)) {
            throw new UnauthorizedDeleteException();
        }

        // 유치원 경력 개월 수 업데이트
        int newCareerMonths = calculateCareerMonths(
            user, 
            workHistory.getStartDate(), 
            workHistory.getEndDate(), 
            false
        );
        userService.updateCareer(user, String.valueOf(newCareerMonths));

        // 유치원 근무 이력 삭제
        workHistoryRepository.delete(workHistory);
    }
} 