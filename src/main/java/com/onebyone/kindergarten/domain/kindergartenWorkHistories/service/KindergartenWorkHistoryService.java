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

    @Transactional
    public KindergartenWorkHistoryResponse addCertification(String email, KindergartenWorkHistoryRequest request) {
        Kindergarten kindergarten = kindergartenRepository.findByName(request.getKindergartenName())
                .orElseThrow(KindergartenNotFoundException::new);

        User user = userService.getUserByEmail(email);
        
        int newCareerMonths = calculateCareerMonths(
            user, 
            request.getStartDate(),
            request.getEndDate(), 
            true
        );

        /// 경력 개월 수 업데이트
        userService.updateCareer(user, String.valueOf(newCareerMonths));

        KindergartenWorkHistory workHistory = request.toEntity(user, kindergarten);
        return KindergartenWorkHistoryResponse.from(workHistoryRepository.save(workHistory));
    }

    public List<KindergartenWorkHistoryResponse> getCertification(String email) {

        User user = userService.getUserByEmail(email);

        return workHistoryRepository.findByUserOrderByStartDateDesc(user)
                .stream()
                .map(KindergartenWorkHistoryResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteCertification(String email, Long historyId) {
        KindergartenWorkHistory workHistory = workHistoryRepository.findById(historyId)
                .orElseThrow(WorkHistoryNotFoundException::new);

        User user = userService.getUserByEmail(email);
        
        if (!workHistory.getUser().equals(user)) {
            throw new UnauthorizedDeleteException();
        }

        int newCareerMonths = calculateCareerMonths(
            user, 
            workHistory.getStartDate(), 
            workHistory.getEndDate(), 
            false
        );

        /// 경력 개월 수 업데이트
        userService.updateCareer(user, String.valueOf(newCareerMonths));

        workHistoryRepository.delete(workHistory);
    }
} 